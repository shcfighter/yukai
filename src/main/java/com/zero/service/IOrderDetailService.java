package com.zero.service;

import com.zero.model.OrderDetail;

import java.util.List;

public interface IOrderDetailService {

    List<OrderDetail> getOrderDetailByOrderId(int orderId);
}
