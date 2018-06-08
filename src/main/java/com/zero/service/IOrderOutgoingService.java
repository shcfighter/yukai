package com.zero.service;

import com.zero.model.OrderOutgoing;

import java.util.List;

public interface IOrderOutgoingService {

    List<OrderOutgoing> findOrderOutgoingByPage(String sampleCode, String orderCode, String productName, Integer status, Integer pageNum, Integer pageSize);

    long findOrderOutgoingRowNum(String sampleCode, String orderCode, String productName, Integer status);

    int insert(OrderOutgoing orderOutgoing, int loginId);

    int update(OrderOutgoing orderOutgoing, int loginId);

    int delete(int id, int loginId);

    int updateStatus(int id, int newStatus, int loginId, Integer... oldStatus);

    OrderOutgoing getOrderOutgoingById(int id);

}
