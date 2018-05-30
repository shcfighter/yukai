package com.zero.web;

import com.zero.common.Result;
import com.zero.common.enmu.GoodsGoodsType;
import com.zero.common.enmu.PurchaseOrderStatus;
import com.zero.common.utils.DateUtils;
import com.zero.common.utils.SessionUtils;
import com.zero.common.utils.excel.SimpleExcelExporter;
import com.zero.model.PurchaseOrder;
import com.zero.service.IPurchaseOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

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
	 * @param goodsName
	 * @param goodsModel
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/findPurchaseOrderPage")
	public Result<List<PurchaseOrder>> findPurchaseOrderPage(String goodsName, String goodsModel, Integer status,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(purchaseOrderService.findPurchaseOrderRowNum(goodsName, goodsModel, status, GoodsGoodsType.PURCHASE),
				purchaseOrderService.findPurchaseOrderByPage(goodsName, goodsModel, status, GoodsGoodsType.PURCHASE, pageNum, pageSize));
	}

	/**
	 * 成品入库申请列表
	 * @param goodsName
	 * @param goodsModel
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/findProductPage")
	public Result<List<PurchaseOrder>> findProductPage(String goodsName, String goodsModel, Integer status,
															 @RequestParam(value = "page", defaultValue = "1") int pageNum,
															 @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(purchaseOrderService.findPurchaseOrderRowNum(goodsName, goodsModel, status, GoodsGoodsType.PRODUCT),
				purchaseOrderService.findPurchaseOrderByPage(goodsName, goodsModel, status, GoodsGoodsType.PRODUCT, pageNum, pageSize));
	}

	/**
	 * 仓库审核列表
	 * @param goodsName
	 * @param goodsModel
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/findPage")
	public Result<List<PurchaseOrder>> findPage(String goodsName, String goodsModel, Integer status,
													   @RequestParam(value = "page", defaultValue = "1") int pageNum,
													   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(purchaseOrderService.findPurchaseOrderRowNum(goodsName, goodsModel, status, null),
				purchaseOrderService.findPurchaseOrderByPage(goodsName, goodsModel, status, null, pageNum, pageSize));
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

	@DeleteMapping("/deletePurchaseOrder/{id}")
	public Result<String> deletePurchaseOrder(HttpServletRequest request,
									   @PathVariable("id") int id){
		if(purchaseOrderService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("删除采购单失败！");
		}
		return Result.resultSuccess("删除采购单成功！");
	}

	@PutMapping("/updateToAudit/{id}")
	public Result<String> updateToAudit(HttpServletRequest request,
									   @PathVariable("id") int id){
		if(purchaseOrderService.updateStatus(id, PurchaseOrderStatus.AUDIT.getKey(), SessionUtils.getCurrentUserId(request),
				PurchaseOrderStatus.SAVE.getKey(), PurchaseOrderStatus.REJECT.getKey()) <= 0){
			return Result.resultFailure("采购单提交审批失败！");
		}
		return Result.resultSuccess("采购单提交审批成功！");
	}

	@PutMapping("/updateToReject/{id}")
	public Result<String> updateToReject(HttpServletRequest request,
										@PathVariable("id") int id){
		if(purchaseOrderService.updateStatus(id, PurchaseOrderStatus.REJECT.getKey(), SessionUtils.getCurrentUserId(request), PurchaseOrderStatus.AUDIT.getKey()) <= 0){
			return Result.resultFailure("采购单打回失败！");
		}
		return Result.resultSuccess("采购单打回成功！");
	}

	@GetMapping("/purchaseOrderExport")
	public void purchaseOrderExport(HttpServletResponse response, HttpServletRequest request,
									String goodsName, String goodsModel, Integer status){
		List<PurchaseOrder> list = purchaseOrderService.findPurchaseOrderByPage(goodsName, goodsModel, status, null, null, null);
		try (OutputStream os = response.getOutputStream()) {
			SimpleExcelExporter excelExporter = new SimpleExcelExporter(list, PurchaseOrder.class);
			String filename = "采购单列表_" + DateUtils.getStringFormat(new Date(), DateUtils.DATEFORMAT2) + ".xlsx";
			String header = request.getHeader("User-Agent").toUpperCase();
			if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
				filename = URLEncoder.encode(filename, "UTF-8");
			} else {
				filename = new String(filename.getBytes(), "ISO8859-1");
			}
			response.reset();
			response.setContentType("application/octet-stream; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" +  filename);
			excelExporter.export(os);
		} catch (Exception ex) {
			LOGGER.error("采购单导出失败:", ex);
			Result.resultFailure("导出错误");
		}
	}

}
