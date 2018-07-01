package com.zero.model.verify;

import com.zero.model.PurchaseOrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class MaterialInboundDetails {
    private List<PurchaseOrderDetail> materialList;
}
