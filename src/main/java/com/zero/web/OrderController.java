package com.zero.web;

import com.zero.common.Result;
import com.zero.common.enmu.OrderStatus;
import com.zero.common.enmu.PlanStatus;
import com.zero.common.utils.DateUtils;
import com.zero.common.utils.SessionUtils;
import com.zero.common.utils.excel.SimpleExcelExporter;
import com.zero.model.Order;
import com.zero.service.IOrderService;
import com.zero.service.IPlanService;
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
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/order")
public class OrderController {
	private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

	@Resource
	IOrderService orderService;
	@Resource
	private IPlanService planService;

	@GetMapping("/findOrderPage")
	public Result<List<Order>> findOrderPage(String productName, String cooperationCompany, Integer status,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(orderService.findOrderRowNum(productName, cooperationCompany, status),
				orderService.findOrderByPage(productName, cooperationCompany, status, pageNum, pageSize));
	}

	@PostMapping("/insertOrder")
	public Result<String> insertOrder(HttpServletRequest request, @RequestBody Order order,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增订单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(orderService.insert(order, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增订单失败！");
		}
		return Result.resultSuccess("新增订单成功！");
	}

	/*@PostMapping("/updateStatus/{orderId}")
	public Result<String> updateStatus(HttpServletRequest request,
									   @PathVariable("orderId") int orderId,
									   int newStatus, int oldStatus){
		if(orderService.updateStatus(orderId, newStatus, oldStatus, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改订单状态失败！");
		}
		return Result.resultSuccess("修改订单状态成功！");
	}*/

	@PutMapping("/updateToFinish/{orderId}")
	public Result<String> updateToFinish(HttpServletRequest request,
									   @PathVariable("orderId") int orderId){
		AtomicBoolean isFinish = new AtomicBoolean(true);
		planService.findPlan(orderId, null).forEach(p -> {
			if(p.getStatus().intValue() != PlanStatus.FINISHED.getKey()){
				isFinish.set(false);
			}
		});
		if(!isFinish.get()){
			return Result.resultFailure("订单入库失败，还有未完成的生产计划！");
		}
		if(orderService.updateStatus(orderId, OrderStatus.STORAGE.getKey(), OrderStatus.PRODUCE.getKey(), SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("订单入库失败！");
		}
		return Result.resultSuccess("订单入库成功！");
	}

	@GetMapping("/orderExport")
	public void orderExport(HttpServletResponse response, HttpServletRequest request,
							String productName, String cooperationCompany, Integer status){
		List<Order> list = orderService.findOrderByPage(productName, cooperationCompany, status, null, null);
		try (OutputStream os = response.getOutputStream()) {
			SimpleExcelExporter excelExporter = new SimpleExcelExporter(list, Order.class);
			String filename = "订单列表_" + com.zero.common.utils.DateUtils.getStringFormat(new Date(), DateUtils.DATEFORMAT2) + ".xlsx";
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
			LOGGER.error("订单导出失败:", ex);
			Result.resultFailure("导出错误");
		}
	}

}
