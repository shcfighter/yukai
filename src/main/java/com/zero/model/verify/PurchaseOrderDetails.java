package com.zero.model.verify;

import com.zero.model.PurchaseOrder;
import com.zero.model.PurchaseOrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderDetails {

    private PurchaseOrder purchaseOrder;
    private List<PurchaseOrderDetail> detailList;
}
