package com.zero.web;

import com.zero.common.Result;
import com.zero.common.enmu.SampleStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Sample;
import com.zero.service.ISampleService;
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
	ISampleService sampleService;

	@GetMapping("/findSample/{orderId}")
	public Result<Sample> findSample(@PathVariable("orderId") int orderId){
		return Result.resultSuccess(sampleService.findSample(orderId));
	}

	@GetMapping("/findSampleDetail/{sampleId}")
	public Result<Map<String, Object>> findSampleDetail(@PathVariable("sampleId") int sampleId){
		return Result.resultSuccess(sampleService.findSampleById(sampleId));
	}

	@PostMapping("/insertSample")
	public Result<String> insertSample(HttpServletRequest request, Sample simple,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增样品信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		int result = sampleService.insertOrUpdate(simple, SessionUtils.getCurrentUserId(request));
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
															@RequestParam(value = "page", defaultValue = "1") int pageNum,
															@RequestParam(value = "limit", defaultValue = "10") int pageSize){
		if(Objects.nonNull(beginDate) && Objects.nonNull(endDate) && beginDate.compareTo(endDate) > 0){
			return Result.resultFailure("开始时间不能大于结束时间");
		}
		return Result.resultSuccess(sampleService.findSampleRowNum(sampleName, sampleCode, company, beginDate, endDate),
				sampleService.findSamplePage(sampleName, sampleCode, company, beginDate, endDate, pageNum, pageSize));
	}

	/**
	 * 提交 进入计划科
	 * @param id
	 * @param request
	 * @return
	 */
	@PostMapping("/updateFinish/{id}")
	public Result<String> updateFinish(@PathVariable("id") int id, HttpServletRequest request) {
		if(sampleService.updateStatus(id, SampleStatus.FINISHED.getKey(), SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("操作失败！");
		}
		return Result.resultSuccess("操作成功");
	}

	/**
	 * 计划科生成生产计划
	 * @param id
	 * @param request
	 * @return
	 */
	@PostMapping("/updateToPlan/{id}")
	public Result<String> updateToPlan(@PathVariable("id") int id, HttpServletRequest request) {
		if(sampleService.updateStatus(id, SampleStatus.PRODUCE.getKey(), SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("操作失败！");
		}
		return Result.resultSuccess("操作成功");
	}
}
