package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Menu;
import com.zero.model.User;
import com.zero.model.verify.LoginUser;
import com.zero.service.IMenuService;
import com.zero.service.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
	private static final Logger LOGGER = LogManager.getLogger(UserController.class);

	@Autowired
	IUserService userService;
	@Resource
	IMenuService menuService;

	@GetMapping("/findUser")
	public Result<List<User>> findUser(String phone, String loginName, String realName,
                                       @RequestParam(value = "page", defaultValue = "1") int pageNum,
									   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(userService.findRowNum(phone, loginName, realName), userService.findUserPage(phone, loginName, realName, pageNum, pageSize));
	}

	@PostMapping("/insertUser")
	public Result<String> insertUser(HttpServletRequest request, @RequestBody User user, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增用户信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		user.setCreater(SessionUtils.getCurrentUserId(request));
		if(userService.insert(user) <= 0){
			return Result.resultFailure("新增用户失败！");
		}
		return Result.resultSuccess("新增用户成功！");
	}

	@PostMapping("/updateUser")
	public Result<String> updateUser(HttpServletRequest request, @RequestBody User user, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改用户信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(userService.update(user, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改用户失败！");
		}
		return Result.resultSuccess("修改用户成功！");
	}

    @DeleteMapping("/deleteUser/{id}")
    public Result<String> deleteUser(HttpServletRequest request, @PathVariable("id") int id){
        if(userService.deleteUserById(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除用户失败！");
        }
        LOGGER.info("删除用户【{}】信息", id);
        return Result.resultSuccess("删除用户成功！");
    }

    @PostMapping("/batchDeleteUser")
    public Result<String> batchDeleteUser(HttpServletRequest request,
                                          @RequestParam("ids[]") List<Integer> ids){
	    LOGGER.info("batch delete ids: {}", ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
	    if(CollectionUtils.isEmpty(ids)){
            return Result.resultFailure("未选择用户，批量删除用户失败！");
        }
        if(userService.batchDeleteUser(ids, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("批量删除用户失败！");
        }
        LOGGER.info("批量删除用户【{}】信息", ids);
        return Result.resultSuccess("批量删除用户成功！");
    }

	@PutMapping("/login")
	public Result<User> login(HttpServletRequest request, @RequestBody LoginUser user){
		LOGGER.debug("用户【{}】登录", user.getLoginName());
		User currentUser = userService.login(user.getLoginName(), user.getPwd());
		if(Objects.isNull(currentUser)){
			return Result.resultFailure("用户名或密码错误！");
		}
		SessionUtils.putCurrentUser(request, currentUser);
		return Result.resultSuccess(currentUser);
	}

    @PutMapping("/logout")
    public Result<User> logout(HttpServletRequest request){
        LOGGER.debug("【{}】退出登录", SessionUtils.getCurrentUserName(request));
        SessionUtils.removeCurrentUser(request);
        return Result.resultSuccess("退出登录成功");
    }

    @GetMapping("/findUserByRole/{roleId}")
    public Result<List<User>> findUserByRole(@PathVariable("roleId") int roleId){
		return Result.resultSuccess(userService.findUserByRole(roleId));
	}

	@GetMapping("/auth")
	public Result<String> auth(HttpServletRequest request){
		if(Objects.isNull(SessionUtils.getCurrentUser(request))){
			return Result.resultFailure("未登录");
		}
		return Result.resultSuccess("");
	}

	@GetMapping("/findMenus")
	public Result<List<Menu>> findMenus(HttpServletRequest request){
		return Result.resultSuccess(menuService.findMenus(SessionUtils.getCurrentUserId(request)));
	}
}
