package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Plan;
import com.zero.service.IPlanService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/plan/")
public class PlanController {

    private static final Logger LOGGER = LogManager.getLogger(PlanController.class);
    @Resource
    private IPlanService planService;

    @PostMapping("/insertPlan")
    public Result<String> insertPlan(HttpServletRequest request,
                                 @RequestBody Plan plan, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("新增生产计划信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(planService.insert(plan, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("新增生产计划失败！");
        }
        return Result.resultSuccess("新增生产计划成功！");
    }

    @PostMapping("/updatePlan")
    public Result<String> updatePlan(HttpServletRequest request, @RequestBody Plan plan, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("修改生产计划信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(planService.update(plan, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("修改生产计划失败！");
        }
        return Result.resultSuccess("修改生产计划成功！");
    }

    @DeleteMapping("/deletePlan/{id}")
    public Result<String> deletePlan(HttpServletRequest request, @PathVariable("id") int id){
        if(planService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除生产计划失败！");
        }
        LOGGER.info("删除生产计划【{}】信息", id);
        return Result.resultSuccess("删除生产计划成功！");
    }

    @GetMapping("findPlan/{orderId}")
    public Result<List<Plan>> findPlan(@PathVariable("orderId") int orderId, String batchNo){
        return Result.resultSuccess(planService.findPlan(orderId, batchNo));
    }
}
