package com.zero.service;

import com.zero.model.PurchaseOrderDetail;

import java.util.List;

public interface IPurchaseOrderDetailService {

    List<PurchaseOrderDetail> getPurchaseOrderDetailByPurchaseId(int purchaseOrderId);
}
