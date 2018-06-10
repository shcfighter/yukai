package com.zero.service;

import com.zero.model.ProductApplyDetail;
import java.util.List;

public interface IProductApplyDetailService {

    List<ProductApplyDetail> getProductApplyDetailByProductId(int productId);
}
