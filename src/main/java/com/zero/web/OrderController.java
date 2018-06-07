package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.OrderStatus;
import com.zero.common.utils.DateUtils;
import com.zero.common.utils.SessionUtils;
import com.zero.common.utils.excel.SimpleExcelExporter;
import com.zero.model.Order;
import com.zero.model.verify.OrderDetails;
import com.zero.service.IOrderDetailService;
import com.zero.service.IOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {
	private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

	@Resource
	IOrderService orderService;
	@Resource
	private IOrderDetailService orderDetailService;

	@GetMapping("/getOrder/{id}")
	public Result<Order> getOrder(@PathVariable("id") int id){
		return Result.resultSuccess(orderService.getOrderById(id));
	}

	@GetMapping("/getOrderAndDetails/{id}")
	public Result<Map<String, Object>> getOrderAndDetails(@PathVariable("id") int id){
		Map<String, Object> result = Maps.newHashMap();
		result.put("order", orderService.getOrderById(id));
		result.put("orderDetails", orderDetailService.getOrderDetailByOrderId(id));
		return Result.resultSuccess(result);
	}

	@GetMapping("/findOrderPage")
	public Result<List<Order>> findOrderPage(String productName, String cooperationCompany, Integer status,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(orderService.findOrderRowNum(productName, cooperationCompany, status),
				orderService.findOrdersAndOrderDetailList(productName, cooperationCompany, status, pageNum, pageSize));
	}

	@GetMapping("/searchOrder")
	public Result<List<Map<String, Object>>> searchOrder(String code, Integer pageNum, Integer pageSize){
		return Result.resultSuccess(orderService.findOrderByPage(code, null, null, pageNum, pageSize).stream().map(order -> {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", order.getId());
			map.put("code", order.getOrderCode());
			return map;
		}).collect(Collectors.toList()));
	}

	@PostMapping("/insertOrder")
	public Result<String> insertOrder(HttpServletRequest request, @RequestBody OrderDetails orderDetails,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增订单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(orderService.insert(orderDetails, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增订单失败！");
		}
		return Result.resultSuccess("新增订单成功！");
	}

	@PostMapping("/updateOrder")
	public Result<String> updateOrder(HttpServletRequest request, @RequestBody OrderDetails orderDetails,
									  BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改订单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(orderService.update(orderDetails, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改订单失败！");
		}
		return Result.resultSuccess("修改订单成功！");
	}

	@PostMapping("/updateToProduce/{orderId}")
	public Result<String> updateToProduce(HttpServletRequest request,
									   @PathVariable("orderId") int orderId){
		if(orderService.updateStatus(orderId, OrderStatus.SAMPLE.getKey(), OrderStatus.SAVE.getKey(), SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("订单进入生产阶段失败！");
		}
		return Result.resultSuccess("订单进入生产阶段成功！");
	}

	@PutMapping("/updateToFinish/{orderId}")
	public Result<String> updateToFinish(HttpServletRequest request,
									   @PathVariable("orderId") int orderId){
		if(orderService.updateStatus(orderId, OrderStatus.STORAGE.getKey(), OrderStatus.SAMPLE.getKey(), SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("订单入库失败！");
		}
		return Result.resultSuccess("订单入库成功！");
	}

	@DeleteMapping("delete/{id}")
	public Result<String> delete(HttpServletRequest request, @PathVariable("id") int id){
		if(orderService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("删除订单信息失败！");
		}
		return Result.resultSuccess("删除订单信息成功！");
	}

	@PostMapping("batchDelete")
	public Result<String> batchDelete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Result.resultFailure("删除订单信息失败，未选中订单！");
		}
		AtomicInteger success = new AtomicInteger(0);
		ids.forEach(id -> {
			if(orderService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
				success.getAndAdd(1);
			}
		});
		return Result.resultSuccess("成功删除【" + success.get() + "】条订单信息！");
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
