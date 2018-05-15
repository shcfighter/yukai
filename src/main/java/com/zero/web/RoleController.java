package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Role;
import com.zero.service.IRoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
	private static final Logger LOGGER = LogManager.getLogger(RoleController.class);

	@Autowired
	IRoleService roleService;

	@GetMapping("/findRole")
	public Result<List<Role>> findRole(String RoleName, String RoleCode,
									   @RequestParam("page") int pageNum,
									   @RequestParam("limit") int pageSize){
		return Result.resultSuccess(roleService.findRowNum(RoleName, RoleCode), roleService.findRolePage(RoleName, RoleCode, pageNum, pageSize));
	}

	@PostMapping("/insertUser")
	public Result<String> insertRole(HttpServletRequest request, @RequestBody Role role,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增角色信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(roleService.insert(role, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增角色失败！");
		}
		return Result.resultSuccess("新增角色成功！");
	}

	@PostMapping("/updateRole")
	public Result<String> updateRole(HttpServletRequest request, @RequestBody Role role,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改角色信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(roleService.update(role, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改角色失败！");
		}
		return Result.resultSuccess("修改角色成功！");
	}

    @DeleteMapping("/deleteRole/{id}")
    public Result<String> deleteRole(HttpServletRequest request, @PathVariable("id") int id){
        if(roleService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除角色失败！");
        }
        LOGGER.info("删除角色【{}】信息", id);
        return Result.resultSuccess("删除角色成功！");
    }
}
