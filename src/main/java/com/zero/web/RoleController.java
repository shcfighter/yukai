package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Menu;
import com.zero.model.Role;
import com.zero.service.IMenuService;
import com.zero.service.IRoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping({"/role", "/menu"})
public class RoleController {
	private static final Logger LOGGER = LogManager.getLogger(RoleController.class);

	@Resource
	IRoleService roleService;
	@Resource
	IMenuService menuService;

	@GetMapping("/findRole")
	public Result<List<Role>> findRole(String RoleName, String RoleCode,
									   @RequestParam(value = "page", defaultValue = "1") int pageNum,
									   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(roleService.findRowNum(RoleName, RoleCode), roleService.findRolePage(RoleName, RoleCode, pageNum, pageSize));
	}

	@PostMapping("/insertRole")
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

	@GetMapping("/findMenu")
	public Result<List<Menu>> findMenu(){
		return Result.resultSuccess(menuService.findMenuTree(null));
	}

	@GetMapping("/findMenuTree")
	public List<Menu> findMenuTree(Integer roleId){
		return menuService.findMenuTree(roleId);
	}

	@PostMapping("/authMenuToRole/{roleId}")
	public Result<String> authMenuToRole(HttpServletRequest request,
										 @PathVariable("roleId") int roleId,
										 @RequestParam("menuIds[]") List<Integer> menuIds){
		if(CollectionUtils.isEmpty(menuIds)){
			return Result.resultFailure("角色赋权失败，未选中菜单权限");
		}
		if(roleService.authMenuToRole(roleId, menuIds, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("角色赋权失败，请重试！");
		}
		return Result.resultSuccess("角色赋权成功！");
	}

	@PostMapping("/authUserToRole/{roleId}")
	public Result<String> authUserToRole(HttpServletRequest request,
										 @PathVariable("roleId") int roleId,
										 @RequestParam("userId") Integer userId){
		if(Objects.isNull(userId)){
			return Result.resultFailure("用户赋权失败，未选中菜用户");
		}
		if(roleService.authUserToRole(roleId, userId, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("用户赋权失败，请重试！");
		}
		return Result.resultSuccess("用户赋权成功！");
	}

	@PostMapping("/unauthUserToRole/{roleId}")
	public Result<String> unauthUserToRole(HttpServletRequest request,
										 @PathVariable("roleId") int roleId,
										 @RequestParam("userId") Integer userId){
		if(Objects.isNull(userId)){
			return Result.resultFailure("取消用户赋权失败，未选中菜用户");
		}
		if(roleService.unauthUserToRole(roleId, userId) <= 0){
			return Result.resultFailure("取消用户赋权失败，请重试！");
		}
		return Result.resultSuccess("取消用户赋权成功！");
	}
}
