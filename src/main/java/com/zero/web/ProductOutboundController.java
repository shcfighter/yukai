package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.ProductStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.ProductApply;
import com.zero.model.ProductOutbound;
import com.zero.model.verify.ProductOutboundDetails;
import com.zero.service.IProductOutboundDetailService;
import com.zero.service.IProductOutboundService;
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
@RequestMapping("/productOutbound/")
public class ProductOutboundController {

    private static final Logger LOGGER = LogManager.getLogger(ProductOutboundController.class);
    @Resource
    private IProductOutboundService productOutboundService;
    @Resource
    private IProductOutboundDetailService productOutboundDetailService;

    @PostMapping("insertProductOutbound")
    public Result<String> insertProductOutbound(HttpServletRequest request,
                                                @RequestBody ProductOutboundDetails productOutboundDetails, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("新增成品出货单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(productOutboundService.insert(productOutboundDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("新增成品出货单失败！");
        }
        return Result.resultSuccess("新增成品出货单成功！");
    }

    @PostMapping("updateProductOutbound")
    public Result<String> updateProductOutbound(HttpServletRequest request, @RequestBody ProductOutboundDetails productOutboundDetails,
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("修改成品出货单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(productOutboundService.update(productOutboundDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("修改成品出货单失败！");
        }
        return Result.resultSuccess("修改成品出货单成功！");
    }

    @DeleteMapping("deleteProductOutbound/{id}")
    public Result<String> deleteProductOutbound(HttpServletRequest request, @PathVariable("id") int id){
        if(productOutboundService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除成品出货单失败！");
        }
        LOGGER.info("删除成品出货单【{}】信息", id);
        return Result.resultSuccess("删除成品出货单成功！");
    }

    @PostMapping("batchDelete")
    public Result<String> batchDelete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Result.resultFailure("删除成品出货单信息失败，未选中成品出货单！");
        }
        AtomicInteger success = new AtomicInteger(0);
        ids.forEach(id -> {
            if(productOutboundService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
                success.getAndAdd(1);
            }
        });
        return Result.resultSuccess("成功删除【" + success.get() + "】条成品出货单信息！");
    }

    @GetMapping("findProductOutboundPage")
    public Result<List<ProductOutbound>> findProductOutboundPage(String productName, String sampleCode, String orderCode, Integer status,
                                                                 @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                                                 @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(productOutboundService.findProductOutboundRowNum(productName, sampleCode, orderCode, status),
                productOutboundService.findProductOutboundAndDetailList(productName, sampleCode, orderCode, status, pageNum, pageSize));
    }

    @GetMapping("getProductOutboundDetailById/{id}")
    public Result<List<Map<String, Object>>> getProductOutboundDetailById(@PathVariable("id") int id){
        Map<String, Object> result = Maps.newHashMap();
        result.put("productOutbound", productOutboundService.getProductOutboundById(id));
        result.put("detailList", productOutboundDetailService.getProductOutboundDetailByProductId(id));
        return Result.resultSuccess(result);
    }

    @PutMapping("updateToAudit/{id}")
    public Result<String> updateToAudit(HttpServletRequest request, @PathVariable("id") int id){
        if(productOutboundService.updateStatus(id, ProductStatus.AUDIT.getKey(), SessionUtils.getCurrentUserId(request),
                ProductStatus.SAVE.getKey(), ProductStatus.REJECT.getKey()) <= 0){
            return Result.resultFailure("成品出货单提交失败！");
        }
        return Result.resultSuccess("成品出货单提交成功！");
    }

    @PutMapping("updateToReject/{id}")
    public Result<String> updateToReject(HttpServletRequest request, @PathVariable("id") int id){
        if(productOutboundService.updateStatus(id, ProductStatus.REJECT.getKey(), SessionUtils.getCurrentUserId(request), ProductStatus.AUDIT.getKey()) <= 0){
            return Result.resultFailure("成品出货单驳回失败！");
        }
        return Result.resultSuccess("成品出货单驳回成功！");
    }
}
