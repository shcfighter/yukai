package com.zero.model.verify;

import com.zero.model.ProductApply;
import com.zero.model.ProductApplyDetail;
import lombok.Data;

import java.util.List;

@Data
public class ProductApplyDetails {

    private ProductApply productApply;
    private List<ProductApplyDetail> applyDetailList;
}
