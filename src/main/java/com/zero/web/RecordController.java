package com.zero.web;

import com.zero.common.Result;
import com.zero.common.utils.SessionUtils;
import com.zero.model.Goods;
import com.zero.service.IGoodsRecordService;
import com.zero.service.IGoodsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/goodsRecord")
public class RecordController {
    private static final Logger LOGGER = LogManager.getLogger(RecordController.class);
    @Resource
    private IGoodsRecordService goodsRecordService;


    @GetMapping("/findRecordPage")
    public Result<List<Goods>> findGoodsPage(String goodsName, String goodsModel, String batchNo, String user,
                                       @RequestParam(value = "page", defaultValue = "1") int pageSize,
                                       @RequestParam(value = "limit", defaultValue = "10") int pageNum){
        return Result.resultSuccess(goodsRecordService.findRowNum(goodsName, null, batchNo, user),
                goodsRecordService.findGoodsRecordPage(goodsName, null, batchNo, user, pageNum, pageSize));
    }

}
