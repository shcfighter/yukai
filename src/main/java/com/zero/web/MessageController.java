package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Message;
import com.zero.service.IMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/message")
public class MessageController {
	private static final Logger LOGGER = LogManager.getLogger(MessageController.class);

	@Resource
	private IMessageService messageService;

	@GetMapping("/getMessage/{id}")
	public Result<Message> getMessage(@PathVariable("id") int id){
		return Result.resultSuccess(messageService.getMessageById(id));
	}


	@GetMapping("/findMessagePage")
	public Result<List<Message>> findMessagePage(HttpServletRequest request, Integer type, Integer read,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(messageService.findMessageRowNum(SessionUtils.getCurrentUserId(request), type, read),
				messageService.findMessagePage(SessionUtils.getCurrentUserId(request), type, read, pageNum, pageSize));
	}

	@GetMapping("/findNotRead")
	public Result<Long> findNotRead(HttpServletRequest request){
		return Result.resultSuccess(messageService.findNotRead(SessionUtils.getCurrentUserId(request)));
	}

	@PostMapping("/markReadMessage")
	public Result<String> markReadMessage(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids){
		if(messageService.markReadMessage(ids) <= 0){
			return Result.resultFailure("修改站内信失败！");
		}
		return Result.resultSuccess("修改站内信成功！");
	}

	@PostMapping("batchDelete")
	public Result<String> batchDelete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids){
		if(CollectionUtils.isEmpty(ids)){
			return Result.resultFailure("删除站内信失败，未选中站内信！");
		}
		AtomicInteger success = new AtomicInteger(0);
		ids.forEach(id -> {
			if(messageService.delete(id) > 0){
				success.getAndAdd(1);
			}
		});
		return Result.resultSuccess("成功删除【" + success.get() + "】条站内信！");
	}

}
