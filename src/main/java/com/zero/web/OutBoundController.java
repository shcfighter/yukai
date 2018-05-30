package com.zero.web;

import com.zero.common.Result;
import com.zero.common.enmu.OutBoundType;
import com.zero.common.enmu.PurchaseOrderStatus;
import com.zero.common.utils.DateUtils;
import com.zero.common.utils.SessionUtils;
import com.zero.common.utils.excel.SimpleExcelExporter;
import com.zero.model.OutBound;
import com.zero.model.PurchaseOrder;
import com.zero.service.IOutBoundService;
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
@RequestMapping("/outbound/")
public class OutBoundController {
	private static final Logger LOGGER = LogManager.getLogger(OutBoundController.class);

	@Resource
	private IOutBoundService OutBoundService;

	@GetMapping("/getOutBound/{id}")
	public Result<PurchaseOrder> getPurchaseOrder(@PathVariable("id") int id){
		return Result.resultSuccess(OutBoundService.getOutBoundById(id));
	}

	/**
	 * 出库申请申请列表
	 * @param goodsName
	 * @param goodsModel
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/findOutBoundPage")
	public Result<List<OutBound>> findOutBoundPage(String goodsName, String goodsModel, Integer status,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(OutBoundService.findOutBoundRowNum(goodsName, goodsModel, status, OutBoundType.PURCHASE),
				OutBoundService.findOutBoundByPage(goodsName, goodsModel, status, OutBoundType.PURCHASE, pageNum, pageSize));
	}

    /**
     * 成品出库申请
     * @param goodsName
     * @param goodsModel
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/findProductPage")
    public Result<List<OutBound>> findProductPage(String goodsName, String goodsModel, Integer status,
                                                   @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(OutBoundService.findOutBoundRowNum(goodsName, goodsModel, status, OutBoundType.PURCHASE),
                OutBoundService.findOutBoundByPage(goodsName, goodsModel, status, OutBoundType.PURCHASE, pageNum, pageSize));
    }

    @GetMapping("/findPage")
    public Result<List<OutBound>> findPage(String goodsName, String goodsModel, Integer status,
                                                  @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                                  @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(OutBoundService.findOutBoundRowNum(goodsName, goodsModel, status, null),
                OutBoundService.findOutBoundByPage(goodsName, goodsModel, status, null, pageNum, pageSize));
    }

	@PostMapping("/insertOutBound")
	public Result<String> insertOutBound(HttpServletRequest request, @RequestBody OutBound outBound,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增出库申请单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(OutBoundService.insert(outBound, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增出库申请单失败！");
		}
		return Result.resultSuccess("新增出库申请单成功！");
	}

	@PostMapping("/updateOutBound")
	public Result<String> updateOutBound(HttpServletRequest request, @RequestBody OutBound outBound,
									  BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改出库申请单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(OutBoundService.update(outBound, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改出库申请单失败！");
		}
		return Result.resultSuccess("修改出库申请单成功！");
	}

	@DeleteMapping("/deleteOutBound/{id}")
	public Result<String> deleteOutBound(HttpServletRequest request,
									   @PathVariable("id") int id){
		if(OutBoundService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("删除出库申请单失败！");
		}
		return Result.resultSuccess("删除出库申请单成功！");
	}

	@PutMapping("/updateToAudit/{id}")
	public Result<String> updateToAudit(HttpServletRequest request,
									   @PathVariable("id") int id){
		if(OutBoundService.updateStatus(id, SessionUtils.getCurrentUserId(request), PurchaseOrderStatus.AUDIT.getKey(),
                PurchaseOrderStatus.SAVE.getKey(), PurchaseOrderStatus.REJECT.getKey()) <= 0){
			return Result.resultFailure("出库申请单提交审批失败！");
		}
		return Result.resultSuccess("出库申请单提交审批成功！");
	}

	@PutMapping("/updateToReject/{id}")
	public Result<String> updateToReject(HttpServletRequest request,
										@PathVariable("id") int id){
		if(OutBoundService.updateStatus(id, PurchaseOrderStatus.REJECT.getKey(), SessionUtils.getCurrentUserId(request), PurchaseOrderStatus.AUDIT.getKey()) <= 0){
			return Result.resultFailure("出库申请单打回失败！");
		}
		return Result.resultSuccess("出库申请单打回成功！");
	}

	@GetMapping("/purchaseOutBoundExport")
	public void purchaseOutBoundExport(HttpServletResponse response, HttpServletRequest request,
									String goodsName, String goodsModel, Integer status){
		List<OutBound> list = OutBoundService.findOutBoundByPage(goodsName, goodsModel, status, null,null, null);
		try (OutputStream os = response.getOutputStream()) {
			SimpleExcelExporter excelExporter = new SimpleExcelExporter(list, OutBound.class);
			String filename = "出库申请单列表_" + DateUtils.getStringFormat(new Date(), DateUtils.DATEFORMAT2) + ".xlsx";
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
			LOGGER.error("出库申请单导出失败:", ex);
			Result.resultFailure("导出错误");
		}
	}

}
