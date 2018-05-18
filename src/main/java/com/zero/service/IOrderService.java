package com.zero.service;

import com.zero.model.Order;

import java.util.List;

public interface IOrderService {

    List<Order> findOrderByPage(String productName, String cooperationCompany, Integer status, int pageNum, int pageSize);

    long findOrderRowNum(String productName, String cooperationCompany, Integer status);

    int insert(Order order, int loginId);

    int updateStatus(int orderId, int newStatus, int oldStatus, int loginId);

    Order getOrderById(int id);
}
