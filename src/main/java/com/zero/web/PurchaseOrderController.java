package com.zero.web;

import com.zero.common.Result;
import com.zero.common.enmu.PurchaseOrderStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.PurchaseOrder;
import com.zero.service.IPurchaseOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/purchaseOrder/")
public class PurchaseOrderController {
	private static final Logger LOGGER = LogManager.getLogger(PurchaseOrderController.class);

	@Resource
	private IPurchaseOrderService purchaseOrderService;

	@GetMapping("/getPurchaseOrder/{id}")
	public Result<PurchaseOrder> getPurchaseOrder(@PathVariable("id") int id){
		return Result.resultSuccess(purchaseOrderService.getPurchaseOrderById(id));
	}

	/**
	 * 采购单入库申请列表
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/findPurchaseOrderPage")
	public Result<List<PurchaseOrder>> findPurchaseOrderPage(String purchaseCode, String orderCode, String productName, Integer status,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(purchaseOrderService.findPurchaseOrderRowNum(purchaseCode, orderCode, productName, status),
				purchaseOrderService.findPurchaseOrderByPage(purchaseCode, orderCode, productName, status, pageNum, pageSize));
	}

	@PostMapping("/insertPurchaseOrder")
	public Result<String> insertPurchaseOrder(HttpServletRequest request, @RequestBody PurchaseOrder purchaseOrder,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增采购单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(purchaseOrderService.insert(purchaseOrder, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增采购单失败！");
		}
		return Result.resultSuccess("新增采购单成功！");
	}

	@PostMapping("/updatePurchaseOrder")
	public Result<String> updatePurchaseOrder(HttpServletRequest request, @RequestBody PurchaseOrder purchaseOrder,
									  BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改采购单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(purchaseOrderService.update(purchaseOrder, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改采购单失败！");
		}
		return Result.resultSuccess("修改采购单成功！");
	}

	@PostMapping("/deleteBatch")
	public Result<String> deletePurchaseOrder(HttpServletRequest request,
									   @RequestParam("ids[]") List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Result.resultFailure("删除采购单信息失败，未选中采购单！");
		}
		AtomicInteger success = new AtomicInteger(0);
		ids.forEach(id -> {
			if(purchaseOrderService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
				success.getAndAdd(1);
			}
		});
		return Result.resultSuccess("成功删除【" + success.get() + "】条采购单信息！");
	}

	@PutMapping("/updateToPurchase/{id}")
	public Result<String> updateToPurchase(HttpServletRequest request,
										@PathVariable("id") int id){
		if(purchaseOrderService.updateStatus(id, PurchaseOrderStatus.PURCHASING.getKey(), SessionUtils.getCurrentUserId(request),
				PurchaseOrderStatus.SAVE.getKey(), PurchaseOrderStatus.REJECT.getKey()) <= 0){
			return Result.resultFailure("修改采购单环节失败！");
		}
		return Result.resultSuccess("采购单进入采购状态！");
	}

	@PutMapping("/updateToInbound/{id}")
	public Result<String> updateToInbound(HttpServletRequest request,
									   @PathVariable("id") int id){
		if(purchaseOrderService.updateStatus(id, PurchaseOrderStatus.AUDIT.getKey(), SessionUtils.getCurrentUserId(request),
				PurchaseOrderStatus.PURCHASING.getKey()) <= 0){
			return Result.resultFailure("修改采购单环节失败！");
		}
		return Result.resultSuccess("采购单进入入库状态！");
	}

	@PutMapping("/updateToReject/{id}")
	public Result<String> updateToReject(HttpServletRequest request,
										@PathVariable("id") int id){
		if(purchaseOrderService.updateStatus(id, PurchaseOrderStatus.REJECT.getKey(), SessionUtils.getCurrentUserId(request), PurchaseOrderStatus.AUDIT.getKey()) <= 0){
			return Result.resultFailure("采购单打回失败！");
		}
		return Result.resultSuccess("采购单打回成功！");
	}

}
