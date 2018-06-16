package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.MessageType;
import com.zero.common.enmu.ProductStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.ProductOutboundDetailMapper;
import com.zero.mapper.ProductOutboundMapper;
import com.zero.model.AuditDept;
import com.zero.model.ProductOutbound;
import com.zero.model.ProductOutboundDetail;
import com.zero.model.example.ProductOutboundDetailExample;
import com.zero.model.example.ProductOutboundExample;
import com.zero.model.verify.ProductOutboundDetails;
import com.zero.service.IMessageService;
import com.zero.service.IProductOutboundService;
import com.zero.service.IUserService;
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
public class ProductOutboundServiceImpl implements IProductOutboundService {

    private static final Logger LOGGER = LogManager.getLogger(ProductOutboundServiceImpl.class);
    @Resource
    private ProductOutboundMapper productOutboundMapper;
    @Resource
    private ProductOutboundDetailMapper productOutboundDetailMapper;
    @Resource
    private IUserService userService;
    @Resource
    private AuditDept auditDept;
    @Resource
    IMessageService messageService;

    @Transactional
    @Override
    public int insert(ProductOutboundDetails productOutboundDetails, int loginId) {
        ProductOutbound productOutbound = productOutboundDetails.getProductOutbound();
        productOutbound.setCreater(loginId);
        productOutbound.setStatus(ProductStatus.SAVE.getKey());
        productOutbound.setCreateTime(new Date());
        if(productOutboundMapper.insertSelective(productOutbound) <= 0){
            LOGGER.error("新增成品入库申请失败！");
            return 0;
        }
        List<ProductOutboundDetail> detailList = productOutboundDetails.getOutboundDetailList();
        detailList.forEach(d -> d.setOutboundId(productOutbound.getId()));
        productOutboundDetailMapper.insertBatch(detailList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int update(ProductOutboundDetails productOutboundDetails, int loginId) {
        ProductOutbound productOutbound = productOutboundDetails.getProductOutbound();
        ProductOutbound productDb = this.getProductOutboundById(productOutbound.getId());
        if(Objects.isNull(productDb)){
            return 0;
        }
        if(ProductStatus.SAVE.getKey() != productDb.getStatus().intValue()){
            return -1;
        }
        BeanUtils.copyProperties(productOutbound, productDb);
        productDb.setUpdateTime(new Date());
        productDb.setModifier(loginId);
        if(productOutboundMapper.updateByPrimaryKeySelective(productDb) <= 0){
            LOGGER.error("修改成品出库申请失败！");
            return 0;
        }
        ProductOutboundDetailExample detailExample = new ProductOutboundDetailExample();
        ProductOutboundDetailExample.Criteria criteria = detailExample.createCriteria();
        criteria.andOutboundIdEqualTo(productDb.getId());
        productOutboundDetailMapper.deleteByExample(detailExample);
        List<ProductOutboundDetail> detailList = productOutboundDetails.getOutboundDetailList();
        if(CollectionUtils.isEmpty(detailList)){
            LOGGER.info("成品出库详情信息为空");
            return 0;
        }
        detailList.forEach(d -> d.setOutboundId(productDb.getId()));
        productOutboundDetailMapper.insertBatch(detailList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int delete(int id, int loginId) {
        ProductOutbound productOutbound = this.getProductOutboundById(id);
        if(Objects.isNull(productOutbound)){
            return 0;
        }
        if(ProductStatus.SAVE.getKey() != productOutbound.getStatus().intValue()){
            return -1;
        }
        if(productOutboundMapper.updateByPrimaryKeySelective(productOutbound) <= 0){
            return 0;
        }
        productOutbound.setIsDeleted(DeletedEnum.YES.getKey());
        productOutbound.setUpdateTime(new Date());
        productOutbound.setModifier(loginId);
        ProductOutboundDetailExample example = new ProductOutboundDetailExample();
        ProductOutboundDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.YES.getKey());
        ProductOutboundDetail detail = new ProductOutboundDetail();
        detail.setOutboundId(productOutbound.getId());
        productOutboundDetailMapper.updateByExampleSelective(detail, example);

        return 1;
    }

    @Override
    public ProductOutbound getProductOutboundById(int id) {
        return productOutboundMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ProductOutbound> findProductOutboundPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
        ProductOutboundExample example = new ProductOutboundExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        ProductOutboundExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return Optional.ofNullable(productOutboundMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }

    @Override
    public List<ProductOutbound> findProductOutboundAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
        ProductOutboundExample example = new ProductOutboundExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        ProductOutboundExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return Optional.ofNullable(productOutboundMapper.findProductOutboundAndDetailList(example)).orElse(Lists.newArrayList());
    }

    @Override
    public long findProductOutboundRowNum(String productName, String sampleCode, String orderCode, Integer status) {
        ProductOutboundExample example = new ProductOutboundExample();
        ProductOutboundExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return productOutboundMapper.countByExample(example);
    }

    @Override
    public int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus) {
        ProductOutbound productOutbound = this.getProductOutboundById(id);
        if(Objects.isNull(productOutbound)){
            return 0;
        }
        if(!Stream.of(oldStatus).collect(Collectors.toList()).contains(productOutbound.getStatus().intValue())){
            LOGGER.error("采购单【{}】状态【{}】不正确，老状态【{}】！", id, productOutbound.getStatus(), oldStatus);
            return -1;
        }
        productOutbound.setStatus(newStatus);
        productOutbound.setUpdateTime(new Date());
        productOutbound.setModifier(loginId);
        if(productOutboundMapper.updateByPrimaryKeySelective(productOutbound) <= 0){
            return 0;
        }
        if (newStatus == ProductStatus.AUDIT.getKey()) {
            messageService.insert("成品出库申请单入库", "您有一个新成品出库申请单将要出库，请及时处理！", MessageType.BUSINESS.getKey(), loginId, auditDept.getStorage());
        }
        if (newStatus == ProductStatus.FINISHED.getKey()) {
            messageService.insert("成品出库申请单入库", "您的成品出库申请单已经出库，请及时查看！", MessageType.BUSINESS.getKey(), loginId, Arrays.asList(new Integer[]{productOutbound.getCreater()}));
        }
        if (newStatus == ProductStatus.REJECT.getKey()) {
            messageService.insert("成品出库申请单驳回", "您的成品出库申请单已被驳回，请及时处理！", MessageType.BUSINESS.getKey(), loginId, Arrays.asList(new Integer[]{productOutbound.getCreater()}));
        }
        return 1;
    }
}
