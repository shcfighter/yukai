package com.zero.service;

import com.zero.common.enmu.OutBoundType;
import com.zero.model.OutBound;

import java.util.List;

public interface IOutBoundService {

    List<OutBound> findOutBoundByPage(String goodsName, String goodsModel, Integer status, OutBoundType type, Integer pageNum, Integer pageSize);

    long findOutBoundRowNum(String goodsName, String goodsModel, Integer status, OutBoundType type);

    int insert(OutBound order, int loginId);

    int update(OutBound order, int loginId);

    int delete(int id, int loginId);

    int updateStatus(int id, int loginId, int newStatus, Integer ...oldStatus);

    OutBound getOutBoundById(int id);
}
