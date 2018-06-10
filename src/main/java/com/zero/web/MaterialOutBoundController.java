package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.MaterialOutBoundStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.MaterialOutbound;
import com.zero.model.verify.MaterialOutboundDetails;
import com.zero.service.IMaterialOutboundService;
import com.zero.service.IOutboundMaterialService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/materialOutbound/")
public class MaterialOutBoundController {
	private static final Logger LOGGER = LogManager.getLogger(MaterialOutBoundController.class);

	@Resource
	private IMaterialOutboundService materialOutboundService;
	@Resource
	private IOutboundMaterialService outboundMaterialService;

	@GetMapping("/getMaterialOutbound/{id}")
	public Result<MaterialOutbound> getMaterialOutbound(@PathVariable("id") int id){
		Map<String, Object> result = Maps.newHashMap();
		result.put("materialOutbound", materialOutboundService.getMaterialOutboundById(id));
		result.put("materialList", outboundMaterialService.getOutboundMaterialByOutboundId(id));
		return Result.resultSuccess(result);
	}

	/**
	 * 采购单入库申请列表
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/findMaterialOutboundPage")
	public Result<List<MaterialOutbound>> findMaterialOutboundPage(String materialName, String orderCode, String color, Integer status,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(materialOutboundService.findMaterialOutboundRowNum(materialName, orderCode, color, status),
				materialOutboundService.findMaterialOutboundByPage(materialName, orderCode, color, status, pageNum, pageSize));
	}

	@PostMapping("/insertMaterialOutbound")
	public Result<String> insertMaterialOutbound(HttpServletRequest request, @RequestBody MaterialOutboundDetails materialOutboundDetails,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增原材料出货申请单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(materialOutboundService.insert(materialOutboundDetails, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增原材料出货申请单失败！");
		}
		return Result.resultSuccess("新增原材料出货申请单成功！");
	}

	@PostMapping("/updateMaterialOutbound")
	public Result<String> updateMaterialOutbound(HttpServletRequest request, @RequestBody MaterialOutboundDetails materialOutboundDetails,
									  BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改原材料出货申请单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(materialOutboundService.update(materialOutboundDetails, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改原材料出货申请单失败！");
		}
		return Result.resultSuccess("修改原材料出货申请单成功！");
	}

	@PostMapping("/deleteBatch")
	public Result<String> deleteBatch(HttpServletRequest request,
									   @RequestParam("ids[]") List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Result.resultFailure("删除原材料出货申请单信息失败，未选中原材料出货申请单！");
		}
		AtomicInteger success = new AtomicInteger(0);
		ids.forEach(id -> {
			if(materialOutboundService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
				success.getAndAdd(1);
			}
		});
		return Result.resultSuccess("成功删除【" + success.get() + "】条原材料出货申请单信息！");
	}

	@PutMapping("/updateToAudit/{id}")
	public Result<String> updateToAudit(HttpServletRequest request,
										 @PathVariable("id") int id){
		if(materialOutboundService.updateStatus(id, MaterialOutBoundStatus.AUDIT.getKey(), SessionUtils.getCurrentUserId(request),
				MaterialOutBoundStatus.SAVE.getKey(), MaterialOutBoundStatus.REJECT.getKey()) <= 0){
			return Result.resultFailure("原材料出货申请单打回失败！");
		}
		return Result.resultSuccess("原材料出货申请单打回成功！");
	}

	@PutMapping("/updateToReject/{id}")
	public Result<String> updateToReject(HttpServletRequest request,
										@PathVariable("id") int id){
		if(materialOutboundService.updateStatus(id, MaterialOutBoundStatus.REJECT.getKey(), SessionUtils.getCurrentUserId(request), MaterialOutBoundStatus.AUDIT.getKey()) <= 0){
			return Result.resultFailure("原材料出货申请单打回失败！");
		}
		return Result.resultSuccess("原材料出货申请单打回成功！");
	}

}
