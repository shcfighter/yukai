package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Material;
import com.zero.model.verify.MaterialInboundDetails;
import com.zero.service.IMaterialService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/material")
public class MaterialController {
    private static final Logger LOGGER = LogManager.getLogger(MaterialController.class);
    @Resource
    private IMaterialService materialService;

    @GetMapping("/findMaterialPage")
    public Result<List<Material>> findMaterialPage(String productName, String color, String ingredients,
                                                @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(materialService.findRowNum(productName, color, ingredients),
                materialService.findMaterialPage(productName, color, ingredients, pageNum, pageSize));
    }

    @PostMapping("/inbound/{purchaseOrderId}")
    public Result<String> inbound(HttpServletRequest request,
                                  @PathVariable int purchaseOrderId,
                                  @RequestBody MaterialInboundDetails inboundDetails){
        if(materialService.inbound(purchaseOrderId, inboundDetails, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request)) <= 0){
            return Result.resultFailure("物品入库失败，请重试");
        }
        return Result.resultSuccess("物品入库成功！");
    }

    @PutMapping("/outbound/{outboundId}")
    public Result<String> outbound(HttpServletRequest request,
                                   @PathVariable("outboundId") int outboundId){
        Map<String, Object> result = null;
        try {
            result = materialService.outbound(outboundId, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request));
        } catch (Exception e) {
            return Result.resultFailure(e.getMessage());
        }
        if(StringUtils.equals(result.get("success") + "", "0")){
            return Result.resultFailure(result.get("message") + "");
        }
        return Result.resultSuccess("物品出库成功！");
    }

}
