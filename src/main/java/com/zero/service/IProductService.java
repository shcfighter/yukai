package com.zero.service;

import com.zero.model.Product;

import java.util.List;

public interface IProductService {


    Product getProductById(int id);

    List<Product> findProductPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    List<Product> findProductAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    long findProductRowNum(String productName, String sampleCode, String orderCode, Integer status);

    Product findProductByOrderCode(String orderCode);

    int inbound(int productId, int loginId, String loginName);

}
