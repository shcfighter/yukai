package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.OrderBatchMapper;
import com.zero.mapper.OrderDetailMapper;
import com.zero.model.OrderBatch;
import com.zero.model.OrderDetail;
import com.zero.model.example.OrderBatchExample;
import com.zero.model.example.OrderDetailExample;
import com.zero.service.IOrderDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements IOrderDetailService {

    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderBatchMapper orderBatchMapper;

    @Override
    public List<OrderBatch> getOrderBatchByOrderId(int orderId) {
        OrderBatchExample example = new OrderBatchExample();
        OrderBatchExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andOrderIdEqualTo(orderId);
        return orderBatchMapper.findOrderBatchAndOrderDetailList(example);
    }
}
