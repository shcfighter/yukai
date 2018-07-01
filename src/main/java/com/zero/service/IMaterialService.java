package com.zero.service;


import com.zero.model.Material;
import com.zero.model.verify.MaterialInboundDetails;

import java.util.List;
import java.util.Map;

public interface IMaterialService {

	List<Material> findMaterialPage(String productName, String color, String ingredients, Integer pageNum, Integer pageSize);

	long findRowNum(String productName, String color, String ingredients);

	Material getMaterialById(int id);

	int inbound(int purchaseOrderId, MaterialInboundDetails inboundDetails, int loginId, String loginName);

	Map<String, Object> outbound(int outboundId, int loginId, String loginName);

	long selectTotal();

}
