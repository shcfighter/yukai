package com.zero.service;

import com.zero.model.ProductOutbound;
import com.zero.model.verify.ProductOutboundDetails;

import java.util.List;

public interface IProductOutboundService {

    int insert(ProductOutboundDetails productOutboundDetails, int loginId);

    int update(ProductOutboundDetails productOutboundDetails, int loginId);

    int delete(int id, int loginId);

    ProductOutbound getProductOutboundById(int id);

    List<ProductOutbound> findProductOutboundPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    List<ProductOutbound> findProductOutboundAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    long findProductOutboundRowNum(String productName, String sampleCode, String orderCode, Integer status);

    int updateStatus(int id, int newStatus, int loginId, Integer... oldStatus);
}
