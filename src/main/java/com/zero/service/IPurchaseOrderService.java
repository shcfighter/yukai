package com.zero.service;

import com.zero.common.enmu.GoodsGoodsType;
import com.zero.model.PurchaseOrder;
import com.zero.model.verify.PurchaseOrderDetails;

import java.util.List;

public interface IPurchaseOrderService {

    List<PurchaseOrder> findPurchaseOrderByPage(String purchaseCode, String orderCode, String sampleCode, Integer status, Integer pageNum, Integer pageSize);

    long findPurchaseOrderRowNum(String purchaseCode, String orderCode, String sampleCode, Integer status);

    int insert(PurchaseOrderDetails purchaseOrderDetails, int loginId);

    int update(PurchaseOrderDetails purchaseOrderDetails, int loginId);

    int delete(int id, int loginId);

    int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus);

    PurchaseOrder getPurchaseOrderById(int id);

}
