package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.PlanStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Plan;
import com.zero.model.verify.PlanDetails;
import com.zero.service.IPlanDetailService;
import com.zero.service.IPlanMaterialService;
import com.zero.service.IPlanService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plan/")
public class PlanController {

    private static final Logger LOGGER = LogManager.getLogger(PlanController.class);
    @Resource
    private IPlanService planService;
    @Resource
    private IPlanDetailService planDetailService;
    @Resource
    private IPlanMaterialService planMaterialService;

    @PostMapping("insertPlan")
    public Result<String> insertPlan(HttpServletRequest request,
                                     @RequestBody PlanDetails planDetails, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("新增生产计划信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(planService.insert(planDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("新增生产计划失败！");
        }
        return Result.resultSuccess("新增生产计划成功！");
    }

    @PostMapping("updatePlan")
    public Result<String> updatePlan(HttpServletRequest request, @RequestBody PlanDetails planDetails, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("修改生产计划信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(planService.update(planDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("修改生产计划失败！");
        }
        return Result.resultSuccess("修改生产计划成功！");
    }

    @DeleteMapping("deletePlan/{id}")
    public Result<String> deletePlan(HttpServletRequest request, @PathVariable("id") int id){
        if(planService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除生产计划失败！");
        }
        LOGGER.info("删除生产计划【{}】信息", id);
        return Result.resultSuccess("删除生产计划成功！");
    }

    @PostMapping("batchDelete")
    public Result<String> batchDelete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Result.resultFailure("删除生产计划单信息失败，未选中样品！");
        }
        AtomicInteger success = new AtomicInteger(0);
        ids.forEach(id -> {
            if(planService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
                success.getAndAdd(1);
            }
        });
        return Result.resultSuccess("成功删除【" + success.get() + "】条生产计划单信息！");
    }

    @GetMapping("findPlanPage")
    public Result<List<Plan>> findPlanPage(String productName, String sampleCode, String orderCode, Integer status,
                                           @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(planService.findPlanRowNum(productName, sampleCode, orderCode, status),
                planService.findPlanAndDetailList(productName, sampleCode, orderCode, status, pageNum, pageSize));
    }

    @GetMapping("searchPlan")
    public Result<List<Map<String, Object>>> getPlanDetailByPlanId(String code){
        return Result.resultSuccess(planService.findPlanPage(code, null, null, PlanStatus.PRODUCE.getKey(), 1, 50)
                .stream().map(p -> {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", p.getId());
                    map.put("code", p.getProductName());
                    return map;
                }).collect(Collectors.toList()));
    }

    @GetMapping("getPlanDetailByPlanId/{id}")
    public Result<List<Map<String, Object>>> getPlanDetailByPlanId(@PathVariable("id") int planId){
        Map<String, Object> result = Maps.newHashMap();
        result.put("plan", planService.getPlanById(planId));
        result.put("detailList", planDetailService.getPlanDetailByOrderId(planId));
        result.put("materialList", planMaterialService.getPlanMaterialByOrderId(planId));
        return Result.resultSuccess(result);
    }

    /**
     * 进入生产
     * @param request
     * @param id
     * @return
     */
    @PutMapping("updateToProduce/{id}")
    public Result<String> updateToProduce(HttpServletRequest request, @PathVariable("id") int id){
        if(planService.updateToProduce(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("操作生产计划失败！");
        }
        return Result.resultSuccess("操作生产计划成功！");
    }

    @PutMapping("updateToFinish/{id}")
    public Result<String> updateToFinish(HttpServletRequest request, @PathVariable("id") int id){
        if(planService.updateToFinish(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("操作生产计划失败！");
        }
        return Result.resultSuccess("操作生产计划成功！");
    }
}
