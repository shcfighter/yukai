package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.DateUtils;
import com.zero.common.utils.SessionUtils;
import com.zero.common.utils.excel.SimpleExcelExporter;
import com.zero.model.Goods;
import com.zero.service.IGoodsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger LOGGER = LogManager.getLogger(GoodsController.class);
    @Resource
    private IGoodsService goodsService;

    @PostMapping("/insertGoods")
    public Result<String> insertGoods(HttpServletRequest request,
                                      @RequestBody Goods goods,
                                      BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("新增物品信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(goodsService.insert(goods, SessionUtils.getCurrentUserId(request),
                SessionUtils.getCurrentUserName(request)) <= 0){
            return Result.resultFailure("新增物品失败！");
        }
        return Result.resultSuccess("新增物品成功！");
    }

    @GetMapping("/findGoodsPage")
    public Result<List<Goods>> findGoodsPage(String goodsName, String goodsModel, String batchNo, Integer type,
                                       @RequestParam(value = "page", defaultValue = "1") int pageNum,
                                       @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        return Result.resultSuccess(goodsService.findRowNum(goodsName, goodsModel, batchNo, type),
                goodsService.findGoodsPage(goodsName, goodsModel, batchNo, type, pageNum, pageSize));
    }

    @PostMapping("/update/{id}")
    public Result<String> update(HttpServletRequest request,
                                  @PathVariable int id, @RequestBody Goods goods,
                                 BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            LOGGER.error("修改物品信息错误：{}", bindingResult.getFieldError().getDefaultMessage());
            return Result.resultFailure(bindingResult.getFieldError().getDefaultMessage());
        }
        if(goodsService.update(goods, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request)) <= 0){
            return Result.resultFailure("修改物品失败，请重试");
        }
        return Result.resultSuccess("修改物品成功！");
    }

    @PostMapping("/inbound/{purchaseOrderId}")
    public Result<String> inbound(HttpServletRequest request,
                                  @PathVariable int purchaseOrderId){
        if(goodsService.inbound(purchaseOrderId, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request)) <= 0){
            return Result.resultFailure("物品入库失败，请重试");
        }
        return Result.resultSuccess("物品入库成功！");
    }

    @PostMapping("/outbound/{id}")
    public Result<String> outbound(HttpServletRequest request,
                                   @PathVariable int id, int num){
        Goods goods = goodsService.getGoodsById(id);
        if(Objects.isNull(goods)){
            LOGGER.error("物品【{}】不存在！", id);
            return Result.resultFailure("该物品不存在！");
        }
        if(goods.getNum() < num){
            LOGGER.error("物品【{}】库存【{}】不足！", id, goods.getNum());
            return Result.resultFailure("该物品库存不足！");
        }
        if(goodsService.outbound(id, num, SessionUtils.getCurrentUserId(request), SessionUtils.getCurrentUserName(request)) <= 0){
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
