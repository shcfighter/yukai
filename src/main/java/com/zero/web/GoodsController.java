package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.DateUtils;
import com.zero.common.utils.SessionUtils;
import com.zero.common.utils.excel.SimpleExcelExporter;
import com.zero.model.Goods;
import com.zero.service.IGoodsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger LOGGER = LogManager.getLogger(GoodsController.class);
    @Resource
    private IGoodsService goodsService;

    @GetMapping("/findGoodsPage")
    public Result<List<Goods>> findGoodsPage(String goodsName, String goodsModel, String batchNo, Integer type,
                                       @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                       @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(goodsService.findRowNum(goodsName, goodsModel, batchNo, type),
                goodsService.findGoodsPage(goodsName, goodsModel, batchNo, type, pageNum, pageSize));
    }

    @PostMapping("/inbound/{purchaseOrderId}")
    public Result<String> inbound(HttpServletRequest request,
                                  @PathVariable int purchaseOrderId){
        if(goodsService.inbound(purchaseOrderId, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request)) <= 0){
            return Result.resultFailure("物品入库失败，请重试");
        }
        return Result.resultSuccess("物品入库成功！");
    }

    @PostMapping("/outbound/{outboundid}")
    public Result<String> outbound(HttpServletRequest request,
                                   @PathVariable int outboundid){
        int num = goodsService.outbound(outboundid, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request));
        if(num <= -1){
            return Result.resultFailure("库存不足，出库失败！");
        }
        if(num <= 0){
            return Result.resultFailure("物品出库失败，请重试");
        }
        return Result.resultSuccess("物品出库成功！");
    }

    @GetMapping("/goodsExport")
    public void goodsExport(HttpServletResponse response, HttpServletRequest request,
                            String goodsName, String goodsModel, String batchNo, Integer type){
        List<Goods> list = goodsService.findGoodsPage(goodsName, goodsModel, batchNo, type, null, null);
        try (OutputStream os = response.getOutputStream()) {
            SimpleExcelExporter excelExporter = new SimpleExcelExporter(list, Goods.class);
            String filename = "库存列表_" + com.zero.common.utils.DateUtils.getStringFormat(new Date(), DateUtils.DATEFORMAT2) + ".xlsx";
            String header = request.getHeader("User-Agent").toUpperCase();
            if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
                filename = URLEncoder.encode(filename, "UTF-8");
            } else {
                filename = new String(filename.getBytes(), "ISO8859-1");
            }
            response.reset();
            response.setContentType("application/octet-stream; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" +  filename);
            excelExporter.export(os);
        } catch (Exception ex) {
            LOGGER.error("库存导出失败:", ex);
            Result.resultFailure("导出错误");
        }
    }
}
