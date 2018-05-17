package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Dept;
import com.zero.service.IDeptService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {
	private static final Logger LOGGER = LogManager.getLogger(DeptController.class);

	@Autowired
	IDeptService deptService;

	@GetMapping("/findDeptTree")
	public Result<List<Dept>> findDeptTree(){
		return Result.resultSuccess(deptService.findDeptTree());
	}

	@GetMapping("/findDeptPage")
	public Result<List<Dept>> findDeptPage(String deptName, String deptCode,
										   @RequestParam(value = "page", defaultValue = "1") int pageNum,
										   @RequestParam(value = "limit", defaultValue = "10") int pageSize){
		return Result.resultSuccess(deptService.findRowNNum(deptName, deptCode), deptService.findDeptPage(deptName, deptCode, pageNum, pageSize));
	}

	@PostMapping("/insertDept")
	public Result<String> insertDept(HttpServletRequest request, @RequestBody Dept dept,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("新增部门信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(deptService.insert(dept, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("新增部门失败！");
		}
		return Result.resultSuccess("新增部门功！");
	}

	@PostMapping("/updateDept")
	public Result<String> updateDept(HttpServletRequest request, @RequestBody Dept dept,
									 BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			LOGGER.error("修改部门信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
			return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
		}
		if(deptService.update(dept, SessionUtils.getCurrentUserId(request)) <= 0){
			return Result.resultFailure("修改部门失败！");
		}
		return Result.resultSuccess("修改部门成功！");
	}

    @DeleteMapping("/deleteDept/{id}")
    public Result<String> deleteDept(HttpServletRequest request, @PathVariable("id") int id){
        if(deptService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除部门失败！");
        }
        LOGGER.info("删除用户【{}】信息", id);
        return Result.resultSuccess("删除部门成功！");
    }

}
