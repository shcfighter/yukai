package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.SampleStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Sample;
import com.zero.model.verify.SampleDetails;
import com.zero.service.IMaterialService;
import com.zero.service.ISampleDetailService;
import com.zero.service.ISampleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sample")
public class SimpleController {
	private static final Logger LOGGER = LogManager.getLogger(SimpleController.class);

	@Resource
	private ISampleService sampleService;
	@Resource
	private ISampleDetailService sampleDetailService;

	@GetMapping("/findSample/{id}")
	public Result<Sample> findSample(@PathVariable("id") int id){
		Map<String, Object> resultMap = Maps.newHashMap();
		Sample sample = sampleService.findSampleById(id);
		resultMap.put("sample", sample);
		if(Objects.nonNull(sample)){
			resultMap.put("details", sampleDetailService.findSampleDetail(id));
		}
		return Result.resultSuccess(resultMap);
	}

	@PostMapping("/insertSample")
	public Result<String> insertSample(HttpServletRequest request,
									   @RequestBody SampleDetails sampleMaterial,
									   BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增样品信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		sampleMaterial.getSample().setPhotoUrl(sampleMaterial.getSampleUrls());
		int result = sampleService.insertOrUpdate(sampleMaterial.getSample(), sampleMaterial.getDetailList(), SessionUtils.getCurrentUserId(request));
		if(result == 0){
			return Result.resultFailure("新增样品失败！");
		}
		if(result < 0){
			return Result.resultFailure("该样品已经进入计划生产阶段，无法修改！");
		}
		return Result.resultSuccess("新增样品成功！");
	}

	@GetMapping("findSamplePage")
	public Result<List<Sample>> findSamplePage(String style,
												String sampleCode,
												Integer status,
												@RequestParam(value = "page", defaultValue = "1") int pageNum,
												@RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(sampleService.findSampleRowNum(style, sampleCode, status),
				sampleService.findSamplePage(style, sampleCode, status, pageNum, pageSize));
	}

	@GetMapping("searchSample")
	public Result<List<Map<String, Object>>> searchSample(String code){
		return Result.resultSuccess(sampleService.findSamplePage(null, code, SampleStatus.FINISHED.getKey(), 1, 50)
		.stream().map(sample -> {
						Map<String, Object> map = Maps.newHashMap();
						map.put("id", sample.getId());
						map.put("code", sample.getSampleCode());
						return map;
				}).collect(Collectors.toList()));
	}

	@DeleteMapping("delete/{id}")
	public Result<String> delete(HttpServletRequest request, @PathVariable("id") int id){
		if(sampleService.deleteSample(id, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("删除样品信息失败！");
		}
		return Result.resultSuccess("删除样品信息成功！");
	}

	@PostMapping("batchDelete")
	public Result<String> batchDelete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Result.resultFailure("删除样品信息失败，未选中样品！");
		}
		AtomicInteger success = new AtomicInteger(0);
		ids.forEach(id -> {
			if(sampleService.deleteSample(id, SessionUtils.getCurrentUserId(request)) > 0){
				success.getAndAdd(1);
			}
		});
		return Result.resultSuccess("成功删除【" + success.get() + "】条样品信息！");
	}

	@DeleteMapping("deleteSampleDetail/{id}")
	public Result<String> deleteSampleDetail(HttpServletRequest request, @PathVariable("id") int id){
		if(sampleDetailService.deleteSampleDetail(id, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("删除样品尺寸信息失败！");
		}
		return Result.resultSuccess("删除样品尺寸信息成功！");
	}

	@PutMapping("updateToFinished/{id}")
	public Result<String> updateToFinished(HttpServletRequest request, @PathVariable("id") int id){
		if(sampleService.updateToFinished(id, SampleStatus.FINISHED.getKey(),SampleStatus.NEW.getKey() ,SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("样品状态更改失败！");
		}
		return Result.resultSuccess("样品中标成功！");
	}
}
