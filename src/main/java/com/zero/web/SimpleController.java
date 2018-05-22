package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.SampleStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Sample;
import com.zero.model.SampleMaterial;
import com.zero.model.verify.Material;
import com.zero.service.IMaterialService;
import com.zero.service.ISampleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/sample")
public class SimpleController {
	private static final Logger LOGGER = LogManager.getLogger(SimpleController.class);

	@Resource
	private ISampleService sampleService;
	@Resource
	private IMaterialService materialService;

	@GetMapping("/findSample/{orderId}")
	public Result<Sample> findSample(@PathVariable("orderId") int orderId){
		Map<String, Object> resultMap = Maps.newHashMap();
		Sample sample = sampleService.findSample(orderId);
		resultMap.put("sample", sample);
		if(Objects.nonNull(sample)){
			resultMap.put("materials", materialService.findMaterial(sample.getId()));
		}
		return Result.resultSuccess(resultMap);
	}

	@GetMapping("/findSampleDetail/{sampleId}")
	public Result<Map<String, Object>> findSampleDetail(@PathVariable("sampleId") int sampleId){
		return Result.resultSuccess(sampleService.findSampleById(sampleId));
	}

	@PostMapping("/insertSample")
	public Result<String> insertSample(HttpServletRequest request,
									   @RequestBody Material sampleMaterial,
									   BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增样品信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		LOGGER.info("原材料size：{}", sampleMaterial.getMaterialList().size());
		sampleMaterial.getMaterialList().forEach(m -> {
			LOGGER.info("原材料：{}", m);
		});
		LOGGER.info("样品：{}", sampleMaterial.getSample());
		LOGGER.info("图片：{}", sampleMaterial.getSampleUrls());
		sampleMaterial.getSample().setPhotoUrl(sampleMaterial.getSampleUrls());
		int result = sampleService.insertOrUpdate(sampleMaterial.getSample(), sampleMaterial.getMaterialList(), SessionUtils.getCurrentUserId(request));
		if(result == 0){
			return Result.resultFailure("新增样品失败！");
		}
		if(result < 0){
			return Result.resultFailure("该样品已经进入计划生产阶段，无法修改！");
		}
		return Result.resultSuccess("新增样品成功！");
	}

	@GetMapping("findSamplePage")
	public Result<List<Map<String, Object>>> findSamplePage(String sampleName,
															String sampleCode,
															String company,
															Date beginDate,
															Date endDate,
															@RequestParam(value = "status", defaultValue = "0") int status,
															@RequestParam(value = "page", defaultValue = "1") int pageNum,
															@RequestParam(value = "limit", defaultValue = "10") int pageSize){
		if(Objects.nonNull(beginDate) && Objects.nonNull(endDate) && beginDate.compareTo(endDate) > 0){
			return Result.resultFailure("开始时间不能大于结束时间");
		}
		return Result.resultSuccess(sampleService.findSampleRowNum(sampleName, sampleCode, company, status, beginDate, endDate),
				sampleService.findSamplePage(sampleName, sampleCode, company, status, beginDate, endDate, pageNum, pageSize));
	}

	/**
	 * 提交 进入采购科
	 * @param sampleId
	 * @param request
	 * @return
	 */
	@PostMapping("/updateToPurchase/{id}")
	public Result<String> updateToPurchase(@PathVariable("id") int sampleId, HttpServletRequest request) {
		if(sampleService.updateStatus(sampleId, SampleStatus.FINISHED.getKey(), SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("操作失败！");
		}
		return Result.resultSuccess("操作成功");
	}

	/**
	 *	订单采购科进入计划科
	 * @param sampleId
	 * @param request
	 * @return
	 */
	@PostMapping("/updateToPlan/{id}")
	public Result<String> updateToPlan(@PathVariable("id") int sampleId, HttpServletRequest request) {
		if(sampleService.updateStatus(sampleId, SampleStatus.PRODUCE.getKey(), SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("操作失败！");
		}
		return Result.resultSuccess("操作成功");
	}

}
