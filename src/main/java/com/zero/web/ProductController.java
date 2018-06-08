package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Plan;
import com.zero.model.verify.ProductDetails;
import com.zero.service.IProductDetailService;
import com.zero.service.IProductService;
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

@RestController
@RequestMapping("/product/")
public class ProductController {

    private static final Logger LOGGER = LogManager.getLogger(ProductController.class);
    @Resource
    private IProductService productService;
    @Resource
    private IProductDetailService productDetailService;

    @PostMapping("insertProduct")
    public Result<String> insertProduct(HttpServletRequest request,
                                        @RequestBody ProductDetails productDetails, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("新增生产计划信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(productService.insert(productDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("新增生产计划失败！");
        }
        return Result.resultSuccess("新增生产计划成功！");
    }

    @PostMapping("updateProduct")
    public Result<String> updateProduct(HttpServletRequest request, @RequestBody ProductDetails productDetails,
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("修改生产计划信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(productService.update(productDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("修改生产计划失败！");
        }
        return Result.resultSuccess("修改生产计划成功！");
    }

    @DeleteMapping("deleteProduct/{id}")
    public Result<String> deleteProduct(HttpServletRequest request, @PathVariable("id") int id){
        if(productService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
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
            if(productService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
                success.getAndAdd(1);
            }
        });
        return Result.resultSuccess("成功删除【" + success.get() + "】条生产计划单信息！");
    }

    @GetMapping("findProductPage")
    public Result<List<Plan>> findProductPage(String productName, String sampleCode, String orderCode, Integer status,
                                           @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(productService.findProductRowNum(productName, sampleCode, orderCode, status),
                productService.findProductAndDetailList(productName, sampleCode, orderCode, status, pageNum, pageSize));
    }

    @GetMapping("getProductDetailByPlanId/{id}")
    public Result<List<Map<String, Object>>> getProductDetailByPlanId(@PathVariable("id") int planId){
        Map<String, Object> result = Maps.newHashMap();
        result.put("plan", productService.getProductById(planId));
        result.put("detailList", productDetailService.getProductDetailByProductId(planId));
        return Result.resultSuccess(result);
    }

    /*@PutMapping("updateToProduce/{id}")
    public Result<String> updateToProduce(HttpServletRequest request, @PathVariable("id") int id){
        if(productService.updateToProduce(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("操作生产计划失败！");
        }
        return Result.resultSuccess("操作生产计划成功！");
    }

    @PutMapping("updateToFinish/{id}")
    public Result<String> updateToFinish(HttpServletRequest request, @PathVariable("id") int id){
        if(productService.updateToFinish(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("操作生产计划失败！");
        }
        return Result.resultSuccess("操作生产计划成功！");
    }*/
}
