package com.zero.service;

import com.zero.common.enmu.GoodsGoodsType;
import com.zero.model.PurchaseOrder;

import java.util.List;

public interface IPurchaseOrderService {

    List<PurchaseOrder> findPurchaseOrderByPage(String goodsName, String goodsModel, Integer status, GoodsGoodsType goodsType, Integer pageNum, Integer pageSize);

    long findPurchaseOrderRowNum(String goodsName, String goodsModel, Integer status, GoodsGoodsType goodsType);

    int insert(PurchaseOrder purchaseOrder, int loginId);

    int update(PurchaseOrder purchaseOrder, int loginId);

    int delete(int id, int loginId);

    int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus);

    PurchaseOrder getPurchaseOrderById(int id);

}
