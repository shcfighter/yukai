package com.zero.service.impl;

import com.zero.mapper.OrderMapper;
import com.zero.model.Order;
import com.zero.model.example.OrderExample;
import com.zero.service.IOrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements IOrderService {

    private static final Logger LOGGER = LogManager.getLogger(OrderServiceImpl.class);

    @Resource
    private OrderMapper orderMapper;

    @Override
    public List<Order> findOrderByPage(String productName, String cooperationCompany, Integer status, int pageNum, int pageSize) {
        OrderExample example = new OrderExample();
        example.setLimit(pageSize);
        example.setPage(pageNum);
        example.setOrderByClause(" create_time desc");
        OrderExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(cooperationCompany)){
            criteria.andProductNameLike("%" + cooperationCompany + "%");
        }
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        return orderMapper.selectByExample(example);
    }

    @Override
    public long findOrderRowNum(String productName, String cooperationCompany, Integer status) {
        OrderExample example = new OrderExample();
        OrderExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(cooperationCompany)){
            criteria.andProductNameLike("%" + cooperationCompany + "%");
        }
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        return orderMapper.countByExample(example);
    }

    @Override
    public int insert(Order order, int loginId) {
        order.setCreater(loginId);
        order.setCreateTime(new Date());
        return orderMapper.insertSelective(order);
    }

    @Override
    public int updateStatus(int orderId, int newStatus, int oldStatus, int loginId) {
        LOGGER.info("用户【{}】更新订单【{}】状态由【{}】改为【{}】", loginId, orderId, oldStatus, newStatus);
        return orderMapper.updateStatus(orderId, newStatus, oldStatus);
    }

    @Override
    public Order getOrderById(int id) {
        return orderMapper.selectByPrimaryKey(id);
    }
}
