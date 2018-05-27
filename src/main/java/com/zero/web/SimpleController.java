package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Sample;
import com.zero.model.verify.Material;
import com.zero.service.IMaterialService;
import com.zero.service.ISampleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

	@GetMapping("/findSample/{id}")
	public Result<Sample> findSample(@PathVariable("id") int id){
		Map<String, Object> resultMap = Maps.newHashMap();
		Sample sample = sampleService.findSampleById(id);
		resultMap.put("sample", sample);
		if(Objects.nonNull(sample)){
			resultMap.put("materials", materialService.findMaterial(id));
		}
		return Result.resultSuccess(resultMap);
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
	public Result<List<Sample>> findSamplePage(String sampleName,
												String sampleCode,
												Integer status,
												@RequestParam(value = "page", defaultValue = "1") int pageNum,
												@RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(sampleService.findSampleRowNum(sampleName, sampleCode, status),
				sampleService.findSamplePage(sampleName, sampleCode, status, pageNum, pageSize));
	}

	@DeleteMapping("delete/{id}")
	public Result<String> delete(HttpServletRequest request, @PathVariable("id") int id){
		if(sampleService.deleteSample(id, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("删除样品信息失败！");
		}
		return Result.resultSuccess("删除样品信息成功！");
	}

}
