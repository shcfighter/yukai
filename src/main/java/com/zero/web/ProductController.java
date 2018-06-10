package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Material;
import com.zero.model.Product;
import com.zero.service.IMaterialService;
import com.zero.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {
    private static final Logger LOGGER = LogManager.getLogger(ProductController.class);
    @Resource
    private IProductService productService;

    @GetMapping("/findProductPage")
    public Result<List<Product>> findProductPage(String productName, String sampleCode, String orderCode, Integer status,
                                                 @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                                 @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(productService.findProductRowNum(productName, sampleCode, orderCode, status),
                productService.findProductAndDetailList(productName, sampleCode, orderCode, status, pageNum, pageSize));
    }

    @PutMapping("/inbound/{productId}")
    public Result<String> inbound(HttpServletRequest request,
                                  @PathVariable int productId){
        if(productService.inbound(productId, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request)) <= 0){
            return Result.resultFailure("成品入库失败，请重试");
        }
        return Result.resultSuccess("成品入库成功！");
    }

    @PutMapping("/outbound/{outboundId}")
    public Result<String> outbound(HttpServletRequest request,
                                   @PathVariable("outboundId") int outboundId){
        Map<String, Object> result = null;
        try {
            result = productService.outbound(outboundId, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request));
        } catch (Exception e) {
            return Result.resultFailure(e.getMessage());
        }
        if(StringUtils.equals(result.get("success") + "", "0")){
            return Result.resultFailure(result.get("message") + "");
        }
        return Result.resultSuccess("成品出库成功！");
    }

}
