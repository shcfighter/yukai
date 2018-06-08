package com.zero.service;

import com.zero.model.Product;
import com.zero.model.verify.ProductDetails;
import java.util.List;

public interface IProductService {

    int insert(ProductDetails productDetails, int loginId);

    int update(ProductDetails productDetails, int loginId);

    int delete(int id, int loginId);

    Product getProductById(int id);

    List<Product> findProductPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    List<Product> findProductAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    long findProductRowNum(String productName, String sampleCode, String orderCode, Integer status);

}
