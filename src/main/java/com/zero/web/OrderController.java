package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.OrderStatus;
import com.zero.common.utils.DateUtils;
import com.zero.common.utils.SessionUtils;
import com.zero.common.utils.excel.SimpleExcelExporter;
import com.zero.model.Order;
import com.zero.service.IOrderService;
import com.zero.service.ISampleService;
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
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/order")
public class OrderController {
	private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

	@Resource
	IOrderService orderService;
	@Resource
	private ISampleService sampleService;

	@GetMapping("/getOrder/{id}")
	public Result<Order> getOrder(@PathVariable("id") int id){
		Map<String, Object> resultMap = Maps.newHashMap();
		Order order = orderService.getOrderById(id);
		resultMap.put("order", order);
		if(Objects.nonNull(order) && Objects.nonNull(order.getSampleId())){
			resultMap.put("sample", sampleService.findSampleById(order.getSampleId()));
		}
		return Result.resultSuccess(resultMap);
	}

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

	@PostMapping("/updateOrder")
	public Result<String> updateOrder(HttpServletRequest request, @RequestBody Order order,
									  BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改订单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(orderService.update(order, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改订单失败！");
		}
		return Result.resultSuccess("修改订单成功！");
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
