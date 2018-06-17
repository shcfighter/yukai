package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.common.enmu.ProductStatus;
import com.zero.common.utils.SessionUtils;
import com.zero.model.ProductApply;
import com.zero.model.ProductApplyDetail;
import com.zero.model.verify.ProductApplyDetails;
import com.zero.service.IProductApplyDetailService;
import com.zero.service.IProductApplyService;
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
@RequestMapping("/productApply/")
public class ProductApplyController {

    private static final Logger LOGGER = LogManager.getLogger(ProductApplyController.class);
    @Resource
    private IProductApplyService productApplyService;
    @Resource
    private IProductApplyDetailService productApplyDetailService;

    @PostMapping("insertProductApply")
    public Result<String> insertProductApply(HttpServletRequest request,
                                        @RequestBody ProductApplyDetails productDetails, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("新增成品入库单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(productApplyService.insert(productDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("新增成品入库单失败！");
        }
        return Result.resultSuccess("新增成品入库单成功！");
    }

    @PostMapping("updateProductApply")
    public Result<String> updateProductApply(HttpServletRequest request, @RequestBody ProductApplyDetails productDetails,
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("修改成品入库单信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(productApplyService.update(productDetails, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("修改成品入库单失败！");
        }
        return Result.resultSuccess("修改成品入库单成功！");
    }

    @DeleteMapping("deleteProductApply/{id}")
    public Result<String> deleteProductApply(HttpServletRequest request, @PathVariable("id") int id){
        if(productApplyService.delete(id, SessionUtils.getCurrentUserId(request)) <= 0){
            return Result.resultFailure("删除成品入库单失败！");
        }
        LOGGER.info("删除成品出库单【{}】信息", id);
        return Result.resultSuccess("删除成品入库单成功！");
    }

    @PostMapping("batchDelete")
    public Result<String> batchDelete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids){
        if(CollectionUtils.isEmpty(ids)){
            return Result.resultFailure("删除成品入库单信息失败，未选中成品入库单！");
        }
        AtomicInteger success = new AtomicInteger(0);
        ids.forEach(id -> {
            if(productApplyService.delete(id, SessionUtils.getCurrentUserId(request)) > 0){
                success.getAndAdd(1);
            }
        });
        return Result.resultSuccess("成功删除【" + success.get() + "】条成品入库单信息！");
    }

    @GetMapping("findProductApplyPage")
    public Result<List<ProductApply>> findProductApplyPage(String productName, String sampleCode, String orderCode, Integer status,
                                                           @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                                           @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(productApplyService.findProductApplyRowNum(productName, sampleCode, orderCode, status),
                productApplyService.findProductApplyAndDetailList(productName, sampleCode, orderCode, status, pageNum, pageSize));
    }

    @GetMapping("getProductApplyDetailById/{id}")
    public Result<List<Map<String, Object>>> getProductApplyDetailById(@PathVariable("id") int id){
        Map<String, Object> result = Maps.newHashMap();
        result.put("productApply", productApplyService.getProductApplyById(id));
        result.put("detailList", productApplyDetailService.getProductApplyDetailByProductId(id));
        return Result.resultSuccess(result);
    }

    @GetMapping("getProductDetailById/{id}")
    public Result<List<ProductApplyDetail>> getProductDetailById(@PathVariable("id") int id){
        return Result.resultSuccess(productApplyDetailService.getProductApplyDetailByProductId(id));
    }

    @PutMapping("updateToAudit/{id}")
    public Result<String> updateToAudit(HttpServletRequest request, @PathVariable("id") int id){
        if(productApplyService.updateStatus(id, ProductStatus.AUDIT.getKey(), SessionUtils.getCurrentUserId(request),
                ProductStatus.SAVE.getKey(), ProductStatus.REJECT.getKey()) <= 0){
            return Result.resultFailure("成品入库单提交失败！");
        }
        return Result.resultSuccess("成品入库单提交成功！");
    }

    @PutMapping("updateToReject/{id}")
    public Result<String> updateToReject(HttpServletRequest request, @PathVariable("id") int id){
        if(productApplyService.updateStatus(id, ProductStatus.REJECT.getKey(), SessionUtils.getCurrentUserId(request), ProductStatus.AUDIT.getKey()) <= 0){
            return Result.resultFailure("成品入库单驳回失败！");
        }
        return Result.resultSuccess("成品入库单驳回成功！");
    }
}
