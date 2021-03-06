package com.zero.service.impl;

import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.OrderStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.OrderBatchMapper;
import com.zero.mapper.OrderDetailMapper;
import com.zero.mapper.OrderMapper;
import com.zero.model.Order;
import com.zero.model.OrderBatch;
import com.zero.model.OrderDetail;
import com.zero.model.example.OrderBatchExample;
import com.zero.model.example.OrderDetailExample;
import com.zero.model.example.OrderExample;
import com.zero.model.verify.OrderDetails;
import com.zero.service.IOrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements IOrderService {

    private static final Logger LOGGER = LogManager.getLogger(OrderServiceImpl.class);

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderBatchMapper orderBatchMapper;

    @Override
    public List<Order> findOrderByPage(String orderCode, String sampleCode, List<Integer> status, Integer pageNum, Integer pageSize) {
        OrderExample example = new OrderExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setLimit(pageSize);
            example.setPage(pageNum);
        }
        example.setOrderByClause(" create_time desc");
        OrderExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if (!CollectionUtils.isEmpty(status)) {
            criteria.andStatusIn(status);
        }
        return orderMapper.selectByExample(example);
    }

    @Override
    public long findOrderRowNum(String orderCode, String sampleCode, List<Integer> status) {
        OrderExample example = new OrderExample();
        OrderExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if (!CollectionUtils.isEmpty(status)) {
            criteria.andStatusIn(status);
        }
        return orderMapper.countByExample(example);
    }

    @Transactional
    @Override
    public int insert(OrderDetails orderDetails, int loginId) {
        Order order = orderDetails.getOrder();
        order.setCreater(loginId);
        order.setCreateTime(new Date());
        order.setIsDeleted(DeletedEnum.NO.getKey());
        if(orderMapper.insertSelective(order) <= 0){
            LOGGER.error("创建订单失败！");
            return 0;
        }
        int orderId = order.getId();
        List<OrderBatch> detailList = orderDetails.getDetailList();
        detailList.forEach(d -> {
            d.setOrderId(orderId);
            d.setCreater(loginId);
            d.setCreateTime(new Date());
            if(orderBatchMapper.insertSelective(d) <= 0){
                LOGGER.error("批量创建op号/交货号失败！");
                throw new BusinessException("批量创建op号/交货号异常");
            } else {
                List<OrderDetail> list = d.getDetails();
                list.forEach(dd -> dd.setOrderId(d.getId()));
                if(orderDetailMapper.insertBatch(list, loginId) != list.size()){
                    LOGGER.error("批量创建尺寸失败！");
                    throw new BusinessException("批量创建尺寸异常");
                }
            }
        });

        return 1;
    }

    @Transactional
    @Override
    public int update(OrderDetails orderDetails, int loginId) {
        Order order = orderDetails.getOrder();
        if(Objects.isNull(order) || Objects.isNull(order.getId())){
            return 0;
        }
        Order orderDb = this.getOrderById(order.getId());
        if (Objects.isNull(order)) {
            return 0;
        }
        BeanUtils.copyProperties(order, orderDb);
        orderDb.setModifier(loginId);
        orderDb.setUpdateTime(new Date());
        if(orderMapper.updateByPrimaryKey(orderDb) <= 0){
            LOGGER.error("修改订单失败！");
            return 0;
        }
        OrderBatchExample batchExample = new OrderBatchExample();
        OrderBatchExample.Criteria criteria2 = batchExample.createCriteria();
        criteria2.andOrderIdEqualTo(order.getId());
        List<OrderBatch> list = orderBatchMapper.selectByExample(batchExample);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(l -> {
                OrderDetailExample detailExample = new OrderDetailExample();
                OrderDetailExample.Criteria criteria = detailExample.createCriteria();
                criteria.andOrderIdEqualTo(l.getId());
                if(orderDetailMapper.deleteByExample(detailExample) <= 0){
                    LOGGER.error("批量删除尺寸失败！");
                    throw new BusinessException("批量删除尺寸异常");
                }
            });
        }
        orderBatchMapper.deleteByExample(batchExample);

        int orderId = order.getId();
        List<OrderBatch> detailList = orderDetails.getDetailList();
        detailList.forEach(d -> {
            d.setOrderId(orderId);
            d.setCreater(loginId);
            d.setCreateTime(new Date());
            if(orderBatchMapper.insertSelective(d) <= 0){
                LOGGER.error("批量创建op号/交货号失败！");
                throw new BusinessException("批量创建op号/交货号异常");
            } else {
                List<OrderDetail> list2 = d.getDetails();
                list2.forEach(dd -> dd.setOrderId(d.getId()));
                if(orderDetailMapper.insertBatch(list2, loginId) != list2.size()){
                    LOGGER.error("批量创建尺寸失败！");
                    throw new BusinessException("批量创建尺寸异常");
                }
            }
        });
        return 1;
    }

    @Override
    public int delete(int id, int loginId) {
        Order order = this.getOrderById(id);
        if (Objects.isNull(order)) {
            return 0;
        }
        if(order.getStatus().intValue() != OrderStatus.SAVE.getKey()){
            LOGGER.error("订单【{}】状态【{}】不能删除！", id, order.getStatus());
            return -1;
        }
        order.setIsDeleted(DeletedEnum.YES.getKey());
        order.setModifier(loginId);
        order.setUpdateTime(new Date());
        if(orderMapper.updateByPrimaryKey(order) <= 0){
            LOGGER.info("删除订单失败！");
            return 0;
        }

        OrderBatchExample batchExample = new OrderBatchExample();
        OrderBatchExample.Criteria criteria2 = batchExample.createCriteria();
        criteria2.andOrderIdEqualTo(order.getId());
        List<OrderBatch> list = orderBatchMapper.selectByExample(batchExample);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(l -> {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setIsDeleted(DeletedEnum.YES.getKey());
                OrderDetailExample updateExample = new OrderDetailExample();
                OrderDetailExample.Criteria criteria = updateExample.createCriteria();
                criteria.andOrderIdEqualTo(l.getId());
                orderDetailMapper.updateByExampleSelective(orderDetail, updateExample);
            });
        }

        OrderBatchExample updateBatchExample = new OrderBatchExample();
        OrderBatchExample.Criteria criteria3 = batchExample.createCriteria();
        criteria2.andOrderIdEqualTo(order.getId());
        OrderBatch orderBatch = new OrderBatch();
        orderBatch.setIsDeleted(DeletedEnum.YES.getKey());
        orderBatchMapper.updateByExampleSelective(orderBatch, updateBatchExample);

        return 1;
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

    @Override
    public List<Order> findOrdersAndOrderDetailList(String orderCode, String sampleCode, List<Integer> status, Integer pageNum, Integer pageSize) {
        OrderExample example = new OrderExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setLimit(pageSize);
            example.setPage(pageNum);
        }
        OrderExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if (!CollectionUtils.isEmpty(status)) {
            criteria.andStatusIn(status);
        }
        return orderMapper.findOrdersAndOrderDetailList(example);
    }
}
