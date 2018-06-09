package com.zero.service;

import com.zero.model.Order;
import com.zero.model.verify.OrderDetails;

import java.util.List;

public interface IOrderService {

    List<Order> findOrderByPage(String orderCode, String sampleCode, List<Integer> status, Integer pageNum, Integer pageSize);

    long findOrderRowNum(String orderCode, String sampleCode, Integer status);

    int insert(OrderDetails orderDetails, int loginId);

    int update(OrderDetails orderDetails, int loginId);

    int delete(int id, int loginId);

    int updateStatus(int orderId, int newStatus, int oldStatus, int loginId);

    Order getOrderById(int id);

    List<Order> findOrdersAndOrderDetailList(String orderCode, String sampleCode, Integer status, Integer pageNum, Integer pageSize);
}
