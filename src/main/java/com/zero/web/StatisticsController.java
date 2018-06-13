package com.zero.web;

import com.google.common.collect.Maps;
import com.zero.common.Result;
import com.zero.service.*;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/statistics/")
public class StatisticsController {

    @Resource
    private IUserService userService;
    @Resource
    private ISampleService sampleService;
    @Resource
    private IOrderService orderService;
    @Resource
    private IDeptService deptService;
    @Resource
    private IMaterialService materialService;
    @Resource
    private IProductDetailService productDetailService;

    @GetMapping("index")
    public Result<Map<String, Object>> statistics(){
        Map<String, Object> result = Maps.newHashMap();
        result.put("user", userService.findRowNum(null, null, null));
        result.put("dept", deptService.findRowNum(null, null));
        result.put("simple", sampleService.findSampleRowNum(null, null, null));
        result.put("order", orderService.findOrderRowNum(null, null, null));
        result.put("material", materialService.selectTotal());
        result.put("product", productDetailService.selectTotal());
        return Result.resultSuccess(result);
    }
}
