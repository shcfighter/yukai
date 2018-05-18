package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Order;
import com.zero.service.IOrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
	private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

	@Autowired
	IOrderService orderService;

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

	@PostMapping("/updateStatus/{orderId}")
	public Result<String> updateStatus(HttpServletRequest request,
									   @PathVariable("orderId") int orderId,
									   int newStatus, int oldStatus){
		if(orderService.updateStatus(orderId, newStatus, oldStatus, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改订单状态失败！");
		}
		return Result.resultSuccess("修改订单状态成功！");
	}

}
