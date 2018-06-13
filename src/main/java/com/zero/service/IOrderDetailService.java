package com.zero.service;

import com.zero.model.OrderBatch;
import com.zero.model.OrderDetail;

import java.util.List;

public interface IOrderDetailService {

    List<OrderBatch> getOrderBatchByOrderId(int orderId);
}
