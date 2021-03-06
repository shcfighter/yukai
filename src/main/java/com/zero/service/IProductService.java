package com.zero.service;

import com.zero.model.Product;

import java.util.List;
import java.util.Map;

public interface IProductService {


    Product getProductById(int id);

    List<Product> findProductPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    List<Map<String, Object>> findProductAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    long findProductRowNum(String productName, String sampleCode, String orderCode, Integer status);

    Product findProductByOrderCode(String orderCode);

    int inbound(int productId, int loginId, String loginName);

    Map<String, Object> outbound(int outboundId, int loginId, String loginName);
}
