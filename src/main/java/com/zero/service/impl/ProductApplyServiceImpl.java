package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.MessageType;
import com.zero.common.enmu.ProductStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.ProductApplyDetailMapper;
import com.zero.mapper.ProductApplyMapper;
import com.zero.model.AuditDept;
import com.zero.model.ProductApply;
import com.zero.model.ProductApplyDetail;
import com.zero.model.PurchaseOrder;
import com.zero.model.example.ProductApplyDetailExample;
import com.zero.model.example.ProductApplyExample;
import com.zero.model.verify.ProductApplyDetails;
import com.zero.service.IMessageService;
import com.zero.service.IProductApplyService;
import com.zero.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductApplyServiceImpl implements IProductApplyService {

    private static final Logger LOGGER = LogManager.getLogger(ProductApplyServiceImpl.class);
    @Resource
    private ProductApplyMapper productApplyMapper;
    @Resource
    private ProductApplyDetailMapper productApplyDetailMapper;
    @Resource
    private IUserService userService;
    @Resource
    private AuditDept auditDept;
    @Resource
    IMessageService messageService;

    @Transactional
    @Override
    public int insert(ProductApplyDetails productDetails, int loginId) {
        ProductApply product = productDetails.getProductApply();
        product.setCreater(loginId);
        product.setStatus(ProductStatus.SAVE.getKey());
        product.setBillingUser(userService.getUserName(loginId));
        product.setCreateTime(new Date());
        if(productApplyMapper.insertSelective(product) <= 0){
            LOGGER.error("新增成品入库申请失败！");
            return 0;
        }
        List<ProductApplyDetail> detailList = productDetails.getApplyDetailList();
        detailList.forEach(d -> d.setProductId(product.getId()));
        productApplyDetailMapper.insertBatch(detailList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int update(ProductApplyDetails productDetails, int loginId) {
        ProductApply product = productDetails.getProductApply();
        ProductApply productDb = this.getProductApplyById(product.getId());
        if(Objects.isNull(productDb)){
            return 0;
        }
        if(ProductStatus.SAVE.getKey() != productDb.getStatus().intValue()){
            return -1;
        }
        BeanUtils.copyProperties(product, productDb);
        productDb.setUpdateTime(new Date());
        productDb.setModifier(loginId);
        if(productApplyMapper.updateByPrimaryKeySelective(productDb) <= 0){
            LOGGER.error("修改成品入库申请失败！");
            return 0;
        }
        ProductApplyDetailExample detailExample = new ProductApplyDetailExample();
        ProductApplyDetailExample.Criteria criteria = detailExample.createCriteria();
        criteria.andProductIdEqualTo(productDb.getId());
        productApplyDetailMapper.deleteByExample(detailExample);
        List<ProductApplyDetail> detailList = productDetails.getApplyDetailList();
        detailList.forEach(d -> d.setProductId(productDb.getId()));
        productApplyDetailMapper.insertBatch(detailList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int delete(int id, int loginId) {
        ProductApply product = this.getProductApplyById(id);
        if(Objects.isNull(product)){
            return 0;
        }
        if(ProductStatus.SAVE.getKey() != product.getStatus().intValue()){
            return -1;
        }
        if(productApplyMapper.updateByPrimaryKeySelective(product) <= 0){
            return 0;
        }
        product.setIsDeleted(DeletedEnum.YES.getKey());
        product.setUpdateTime(new Date());
        product.setModifier(loginId);
        ProductApplyDetailExample example = new ProductApplyDetailExample();
        ProductApplyDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.YES.getKey());
        ProductApplyDetail detail = new ProductApplyDetail();
        detail.setProductId(product.getId());
        productApplyDetailMapper.updateByExampleSelective(detail, example);

        return 1;
    }

    @Override
    public ProductApply getProductApplyById(int id) {
        return productApplyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ProductApply> findProductApplyPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
        ProductApplyExample example = new ProductApplyExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        ProductApplyExample.Criteria criteria = example.createCriteria();
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
        return Optional.ofNullable(productApplyMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }

    @Override
    public List<ProductApply> findProductApplyAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
        ProductApplyExample example = new ProductApplyExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        ProductApplyExample.Criteria criteria = example.createCriteria();
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
        return Optional.ofNullable(productApplyMapper.findProductAndDetailList(example)).orElse(Lists.newArrayList());
    }

    @Override
    public long findProductApplyRowNum(String productName, String sampleCode, String orderCode, Integer status) {
        ProductApplyExample example = new ProductApplyExample();
        ProductApplyExample.Criteria criteria = example.createCriteria();
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
        return productApplyMapper.countByExample(example);
    }

    @Override
    public int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus) {
        ProductApply productApply = this.getProductApplyById(id);
        if(Objects.isNull(productApply)){
            return 0;
        }
        if(!Stream.of(oldStatus).collect(Collectors.toList()).contains(productApply.getStatus().intValue())){
            LOGGER.error("采购单【{}】状态【{}】不正确，老状态【{}】！", id, productApply.getStatus(), oldStatus);
            return -1;
        }
        productApply.setStatus(newStatus);
        productApply.setUpdateTime(new Date());
        productApply.setModifier(loginId);
        if(newStatus == ProductStatus.FINISHED.getKey()){
            productApply.setWarehouseUser(userService.getUserName(loginId));
            productApply.setWarehouseDate(new Date());
        }
        if (productApplyMapper.updateByPrimaryKeySelective(productApply) <= 0) {
            return 0;
        }
        if (newStatus == ProductStatus.AUDIT.getKey()) {
            messageService.insert("成品入库申请单入库", "您有一个新成品入库申请单将要入库，请及时处理！", MessageType.BUSINESS.getKey(), loginId, auditDept.getStorage());
        }
        if (newStatus == ProductStatus.FINISHED.getKey()) {
            messageService.insert("成品入库申请单入库", "您的成品入库申请单已经入库，请及时查看！", MessageType.BUSINESS.getKey(), loginId, Arrays.asList(new Integer[]{productApply.getCreater()}));
        }
        if (newStatus == ProductStatus.REJECT.getKey()) {
            messageService.insert("成品入库申请单驳回", "您的成品入库申请单已被驳回，请及时处理！", MessageType.BUSINESS.getKey(), loginId, Arrays.asList(new Integer[]{productApply.getCreater()}));
        }
        return 1;
    }
}
