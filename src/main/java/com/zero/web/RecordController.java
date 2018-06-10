package com.zero.web;

import com.zero.common.Result;
import com.zero.model.GoodsUseRecord;
import com.zero.service.IGoodsRecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/goodsRecord")
public class RecordController {
    private static final Logger LOGGER = LogManager.getLogger(RecordController.class);
    @Resource
    private IGoodsRecordService goodsRecordService;


    @GetMapping("/findRecordPage")
    public Result<List<GoodsUseRecord>> findGoodsPage(String goodsName, String goodsModel, String batchNo, String user,
                                                      @RequestParam(value = "page", defaultValue = "1") int pageSize,
                                                      @RequestParam(value = "limit", defaultValue = "10") int pageNum){
        return Result.resultSuccess(goodsRecordService.findRowNum(goodsName, null, batchNo, user),
                goodsRecordService.findGoodsRecordPage(goodsName, null, batchNo, user, pageNum, pageSize));
    }

}
