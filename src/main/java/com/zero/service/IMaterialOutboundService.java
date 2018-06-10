package com.zero.service;

import com.zero.model.MaterialOutbound;
import com.zero.model.verify.MaterialOutboundDetails;

import java.util.List;

public interface IMaterialOutboundService {

    List<MaterialOutbound> findMaterialOutboundByPage(String materialName, String orderCode, String color, Integer status, Integer pageNum, Integer pageSize);

    long findMaterialOutboundRowNum(String materialName, String orderCode, String color, Integer status);

    int insert(MaterialOutboundDetails materialOutboundDetails, int loginId);

    int update(MaterialOutboundDetails materialOutboundDetails, int loginId);

    int delete(int id, int loginId);

    int updateStatus(int id, int newStatus, int loginId, Integer... oldStatus);

    MaterialOutbound getMaterialOutboundById(int id);

}
