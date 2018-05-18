package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.OrderStatus;
import com.zero.common.enmu.SampleStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.SampleMapper;
import com.zero.model.Order;
import com.zero.model.Sample;
import com.zero.model.example.SampleExample;
import com.zero.service.IOrderService;
import com.zero.service.ISampleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SampleServiceImpl implements ISampleService {

    private static final Logger LOGGER = LogManager.getLogger(SampleServiceImpl.class);

    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private IOrderService orderService;

    @Override
    public int insertOrUpdate(Sample sample, int loginId) {
        if(Objects.isNull(sample.getId())){
            sample.setCreater(loginId);
            sample.setCreateTime(new Date());
            if(Objects.nonNull(sample.getPhotoUrls())){
                sample.setPhotoUrl(Stream.of(sample.getPhotoUrls()).collect(Collectors.joining(",")));
            }
            return sampleMapper.insertSelective(sample);
        }
        Sample sampleDb = sampleMapper.selectByPrimaryKey(sample.getId());
        if(Objects.isNull(sampleDb)){
            return 0;
        }
        if(!sampleDb.getStatus().equals(SampleStatus.NEW.getKey())){
            LOGGER.error("样品【{}】已经进入计划科无法进行更改！", sampleDb.getId());
            return -1;
        }
        if(Objects.nonNull(sample.getPhotoUrls())){
            sample.setPhotoUrl(Stream.of(sample.getPhotoUrls()).collect(Collectors.joining(",")));
        }
        BeanUtils.copyProperties(sample, sampleDb);
        sampleDb.setModifier(loginId);
        sampleDb.setUpdateTime(new Date());
        return sampleMapper.updateByPrimaryKeySelective(sampleDb);
    }

    @Override
    public Sample findSample(int orderId) {
        SampleExample example = new SampleExample();
        SampleExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        List<Sample> list = Optional.ofNullable(sampleMapper.selectByExample(example)).orElse(Lists.newArrayList());

        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<Map<String, Object>> findSamplePage(String sampleName, String sampleCode, String company, Date beginDate, Date endDate, int page, int pageSize) {
        Map<String, Object> condition = Maps.newHashMap();
        condition.put("status", SampleStatus.FINISHED.getKey());
        condition.put("limit", pageSize);
        condition.put("offset", (page - 1) * pageSize);
        if(StringUtils.isNotEmpty(sampleName)){
            condition.put("sampleName", sampleName);
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            condition.put("sampleCode", sampleCode);
        }
        if(StringUtils.isNotEmpty(company)){
            condition.put("company", company);
        }
        if(Objects.nonNull(beginDate) && Objects.nonNull(endDate)){
            condition.put("beginDate", beginDate);
            condition.put("endDate", endDate);
        }
        return sampleMapper.findSamplePage(condition);
    }

    @Override
    public long findSampleRowNum(String sampleName, String sampleCode, String company, Date beginDate, Date endDate) {
        Map<String, Object> condition = Maps.newHashMap();
        condition.put("status", SampleStatus.FINISHED.getKey());
        if(StringUtils.isNotEmpty(sampleName)){
            condition.put("sampleName", sampleName);
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            condition.put("sampleCode", sampleCode);
        }
        if(StringUtils.isNotEmpty(company)){
            condition.put("company", company);
        }
        if(Objects.nonNull(beginDate) && Objects.nonNull(endDate)){
            condition.put("beginDate", beginDate);
            condition.put("endDate", endDate);
        }
        return sampleMapper.findSampleRowNum(condition);
    }

    @Transactional
    @Override
    public int updateStatus(int id, int status, int loginId) {
        Sample sample = sampleMapper.selectByPrimaryKey(id);
        if(Objects.isNull(sample)){
            return 0;
        }
        int orderId = sample.getOrderId();
        Order order = orderService.getOrderById(orderId);
        if(Objects.isNull(order)){
            LOGGER.error("订单【{}】不存在", orderId);
            return 0;
        }
        if(SampleStatus.FINISHED.getKey() == status){
            if(orderService.updateStatus(orderId, OrderStatus.SAMPLE.getKey(), OrderStatus.SAVE.getKey(), loginId) <= 0){
                LOGGER.error("修改订单【{}】状态失败，{} --> {}", orderId, OrderStatus.SAVE.getDesc(), OrderStatus.SAMPLE.getDesc());
                return 0;
            }
        }
        if(SampleStatus.PRODUCE.getKey() == status){
            if(orderService.updateStatus(orderId, OrderStatus.PLAN.getKey(), OrderStatus.SAMPLE.getKey(), loginId) <= 0){
                LOGGER.error("修改订单【{}】状态失败，{} --> {}", orderId, OrderStatus.SAVE.getDesc(), OrderStatus.SAMPLE.getDesc());
                return 0;
            }
        }
        Map<String, Object> condition = Maps.newHashMap();
        condition.put("id", id);
        condition.put("status", status);
        condition.put("modifier", loginId);
        return sampleMapper.updateStatus(condition);
    }
}
