package com.zero.service;

import com.zero.model.ProductApply;
import com.zero.model.verify.ProductApplyDetails;
import java.util.List;

public interface IProductApplyService {

    int insert(ProductApplyDetails productDetails, int loginId);

    int update(ProductApplyDetails productDetails, int loginId);

    int delete(int id, int loginId);

    ProductApply getProductApplyById(int id);

    List<ProductApply> findProductApplyPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    List<ProductApply> findProductApplyAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    long findProductApplyRowNum(String productName, String sampleCode, String orderCode, Integer status);

    int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus);
}
