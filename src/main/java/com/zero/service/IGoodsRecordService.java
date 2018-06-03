package com.zero.service;

import com.zero.model.GoodsUseRecord;

import java.util.List;

public interface IGoodsRecordService {

    int insert(GoodsUseRecord record, int loginId, String loginName);

    int insert(int goodsId, String goodsName, String goodsModel, String batchNo, int goodsNum, int originalNum,int type , int loginId, int user);

    List<GoodsUseRecord> findGoodsRecordPage(String goodsName, String goodsModel, String batchNo, String user, int pageNum, int pageSize);

    long findRowNum(String goodsName, String goodsModel, String batchNo, String user);
}
