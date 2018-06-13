package com.zero.web;

import com.zero.common.Result;
import com.zero.common.enmu.OrderOutgoingStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.OrderOutgoing;
import com.zero.service.IOrderOutgoingService;
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
@RequestMapping("/orderOutgoing/")
public class OrderOutgoingController {
	private static final Logger LOGGER = LogManager.getLogger(OrderOutgoingController.class);

	@Resource
	private IOrderOutgoingService orderOutgoingService;

	@GetMapping("/getOrderOutgoing/{id}")
	public Result<OrderOutgoing> getOrderOutgoing(@PathVariable("id") int id){
		return Result.resultSuccess(orderOutgoingService.getOrderOutgoingById(id));
	}

	/**
	 * 采购单入库申请列表
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/findOrderOutgoingPage")
	public Result<List<OrderOutgoing>> findOrderOutgoingPage(String sampleCode, String orderCode, String productName, Integer status,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(orderOutgoingService.findOrderOutgoingRowNum(sampleCode, orderCode, productName, status),
				orderOutgoingService.findOrderOutgoingByPage(sampleCode, orderCode, productName, status, pageNum, pageSize));
	}

	@PostMapping("/insertOrderOutgoing")
	public Result<String> insertOrderOutgoing(HttpServletRequest request, @RequestBody OrderOutgoing orderOutgoing,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增外发成品信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(orderOutgoingService.insert(orderOutgoing, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增采购单失败！");
		}
		return Result.resultSuccess("新增采购单成功！");
	}

	@PostMapping("/updateOrderOutgoing")
	public Result<String> updateOrderOutgoing(HttpServletRequest request, @RequestBody OrderOutgoing orderOutgoing,
									  BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改外发成品信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(orderOutgoingService.update(orderOutgoing, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改采购单失败！");
		}
		return Result.resultSuccess("修改采购单成功！");
	}

	@PostMapping("/deleteBatch")
	public Result<String> deleteOrderOutgoing(HttpServletRequest request,
									   @RequestParam("ids[]") List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Result.resultFailure("删除外发成品信息失败，未选中采购单！");
		}
		AtomicInteger success = new AtomicInteger(0);
		ids.forEach(id -> {
			if(orderOutgoingService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
				success.getAndAdd(1);
			}
		});
		return Result.resultSuccess("成功删除【" + success.get() + "】条外发成品信息！");
	}

	@PutMapping("/updateToFinish/{id}")
	public Result<String> updateToFinish(HttpServletRequest request,
										@PathVariable("id") int id){
		if(orderOutgoingService.updateStatus(id, OrderOutgoingStatus.FINISHED.getKey(), SessionUtils.getCurrentUserId(request),
				OrderOutgoingStatus.SAVE.getKey()) <= 0){
			return Result.resultFailure("修改外发成品环节失败！");
		}
		return Result.resultSuccess("外发成品已完成！");
	}

}
