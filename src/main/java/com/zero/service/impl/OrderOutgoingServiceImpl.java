package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.OrderOutgoingStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.OrderOutgoingMapper;
import com.zero.model.OrderOutgoing;
import com.zero.model.example.OrderOutgoingExample;
import com.zero.service.IOrderOutgoingService;
import com.zero.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderOutgoingServiceImpl implements IOrderOutgoingService {

    private static final Logger LOGGER = LogManager.getLogger(OrderOutgoingServiceImpl.class);

    @Resource
    private OrderOutgoingMapper orderOutgoingMapper;
    @Resource
    private IUserService userService;

    @Override
    public List<OrderOutgoing> findOrderOutgoingByPage(String sampleCode, String orderCode, String productName, Integer status, Integer pageNum, Integer pageSize) {
        OrderOutgoingExample example = new OrderOutgoingExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        OrderOutgoingExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        return orderOutgoingMapper.selectByExample(example);
    }

    @Override
    public long findOrderOutgoingRowNum(String sampleCode, String orderCode, String productName, Integer status) {
        OrderOutgoingExample example = new OrderOutgoingExample();
        OrderOutgoingExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        return orderOutgoingMapper.countByExample(example);
    }

    @Override
    public int insert(OrderOutgoing orderOutgoing, int loginId) {
        orderOutgoing.setCreater(loginId);
        orderOutgoing.setCreateTime(new Date());
        orderOutgoing.setUser(userService.getUserName(loginId));
        return orderOutgoingMapper.insertSelective(orderOutgoing);
    }

    @Override
    public int update(OrderOutgoing orderOutgoing, int loginId) {
        if(Objects.isNull(orderOutgoing) || Objects.isNull(orderOutgoing.getId())){
            return 0;
        }
        OrderOutgoing orderOutgoingDb = this.getOrderOutgoingById(orderOutgoing.getId());
        if(Objects.isNull(orderOutgoingDb)){
            return 0;
        }
        BeanUtils.copyProperties(orderOutgoing, orderOutgoingDb);
        orderOutgoingDb.setUpdateTime(new Date());
        orderOutgoingDb.setModifier(loginId);
        return orderOutgoingMapper.updateByPrimaryKeySelective(orderOutgoingDb);
    }

    @Override
    public int delete(int id, int loginId) {
        OrderOutgoing orderOutgoing = this.getOrderOutgoingById(id);
        if(Objects.isNull(orderOutgoing)){
            return 0;
        }
        if(OrderOutgoingStatus.SAVE.getKey() != orderOutgoing.getStatus().intValue()){
            LOGGER.error("外发单【{}】状态【{}】不能删除！", id, orderOutgoing.getStatus());
            return -1;
        }
        orderOutgoing.setIsDeleted(DeletedEnum.YES.getKey());
        orderOutgoing.setUpdateTime(new Date());
        orderOutgoing.setModifier(loginId);
        return orderOutgoingMapper.updateByPrimaryKeySelective(orderOutgoing);
    }

    @Override
    public int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus) {
        OrderOutgoing orderOutgoing = this.getOrderOutgoingById(id);
        if(Objects.isNull(orderOutgoing)){
            return 0;
        }
        if(!Stream.of(oldStatus).collect(Collectors.toList()).contains(orderOutgoing.getStatus().intValue())){
            LOGGER.error("外发单【{}】状态【{}】不正确，老状态【{}】！", id, orderOutgoing.getStatus(), oldStatus);
            return -1;
        }
        orderOutgoing.setStatus(newStatus);
        orderOutgoing.setUpdateTime(new Date());
        orderOutgoing.setModifier(loginId);
        return orderOutgoingMapper.updateByPrimaryKeySelective(orderOutgoing);
    }

    @Override
    public OrderOutgoing getOrderOutgoingById(int id) {
        return orderOutgoingMapper.selectByPrimaryKey(id);
    }
}
