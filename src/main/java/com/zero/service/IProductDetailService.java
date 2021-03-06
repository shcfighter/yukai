package com.zero.service;

import com.zero.model.ProductApplyDetail;
import com.zero.model.ProductDetail;

import java.util.List;

public interface IProductDetailService {

    List<ProductDetail> getProductDetailByProductId(int productId);

    long selectTotal();
}
