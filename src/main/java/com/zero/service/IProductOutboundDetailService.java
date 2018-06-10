package com.zero.service;

import com.zero.model.ProductOutboundDetail;

import java.util.List;

public interface IProductOutboundDetailService {

    List<ProductOutboundDetail> getProductOutboundDetailByProductId(int outboundId);
}
