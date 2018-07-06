package com.zero.service;

import com.zero.model.OrderPrice;

import java.util.List;

public interface IOrderPriceService {
    List<OrderPrice> findOrderPrice(int orderId);

    int insertBatch(List<OrderPrice> list, int orderId, int loginId);

    int insert(OrderPrice orderPrice, int orderId, int loginId);
}
