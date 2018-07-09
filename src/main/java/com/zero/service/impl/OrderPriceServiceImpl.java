package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.OrderPriceMapper;
import com.zero.model.OrderPrice;
import com.zero.model.example.OrderPriceExample;
import com.zero.service.IOrderPriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderPriceServiceImpl implements IOrderPriceService {
    @Resource
    private OrderPriceMapper orderPriceMapper;

    @Override
    public List<OrderPrice> findOrderPrice(int orderId) {
        OrderPriceExample example = new OrderPriceExample();
        example.setOrderByClause(" order_status desc");
        OrderPriceExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        return orderPriceMapper.selectByExample(example);
    }

    @Transactional
    @Override
    public int insertBatch(List<OrderPrice> list, int orderId, int loginId) {
        OrderPriceExample example = new OrderPriceExample();
        OrderPriceExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        orderPriceMapper.deleteByExample(example);
        return orderPriceMapper.insertBatch(list, orderId, loginId);
    }

    @Override
    public int insert(OrderPrice orderPrice, int orderId, int loginId) {
        if(Objects.isNull(orderPrice.getId())){
            orderPrice.setCreater(loginId);
            orderPrice.setOrderId(orderId);
            orderPrice.setCreateTime(new Date());
            return orderPriceMapper.insertSelective(orderPrice);
        }
        OrderPrice orderPriceDb = orderPriceMapper.selectByPrimaryKey(orderPrice.getId());
        if(Objects.isNull(orderPriceDb)){
            return 0;
        }
        BeanUtils.copyProperties(orderPrice, orderPriceDb);
        orderPriceDb.setModifier(loginId);
        orderPriceDb.setUpdateTime(new Date());
        return orderPriceMapper.updateByPrimaryKeySelective(orderPriceDb);
    }
}
