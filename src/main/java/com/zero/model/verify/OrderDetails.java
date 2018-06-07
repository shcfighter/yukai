package com.zero.model.verify;

import com.zero.model.Order;
import com.zero.model.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetails {
    private Order order;
    private List<OrderDetail> detailList;
}
