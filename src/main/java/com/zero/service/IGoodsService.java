package com.zero.service;


import com.zero.model.Goods;
import com.zero.model.User;

import java.util.List;

public interface IGoodsService {

	List<Goods> findGoodsPage(String goodsName, String goodsModel, String batchNo, Integer type, Integer pageNum, Integer pageSize);

	long findRowNum(String goodsName, String goodsModel, String batchNo, Integer type);

	int insert(Goods goods, int loginId, String loginName);

	int update(Goods goods, int loginId, String loginName);

	Goods getGoodsById(int id);

	int deleteGoodsById(int id, int loginId, String loginName);

	int inbound(int purchaseOrderId, int loginId, String loginName);

	int outbound(int id, int num, int loginId, String loginName);

	List<Goods> findGoodsList(String goodsName, String goodsModel, Integer type);

}
