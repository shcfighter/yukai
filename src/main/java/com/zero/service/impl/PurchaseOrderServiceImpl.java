package com.zero.service.impl;

import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.GoodsGoodsType;
import com.zero.common.enmu.MessageType;
import com.zero.common.enmu.PurchaseOrderStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.PurchaseOrderDetailMapper;
import com.zero.mapper.PurchaseOrderMapper;
import com.zero.model.AuditDept;
import com.zero.model.PurchaseOrder;
import com.zero.model.PurchaseOrderDetail;
import com.zero.model.example.PurchaseOrderDetailExample;
import com.zero.model.example.PurchaseOrderExample;
import com.zero.model.verify.PurchaseOrderDetails;
import com.zero.service.IMessageService;
import com.zero.service.IPurchaseOrderService;
import com.zero.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PurchaseOrderServiceImpl implements IPurchaseOrderService {

    private static final Logger LOGGER = LogManager.getLogger(PurchaseOrderServiceImpl.class);

    @Resource
    private PurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    @Resource
    private IUserService userService;
    @Resource
    private IMessageService messageService;
    @Resource
    private AuditDept auditDept;

    @Override
    public List<PurchaseOrder> findPurchaseOrderByPage(String purchaseCode, String orderCode, String sampleCode, Integer status, Integer pageNum, Integer pageSize) {
        PurchaseOrderExample example = new PurchaseOrderExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        example.setOrderByClause(" create_time desc");
        PurchaseOrderExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(purchaseCode)){
            criteria.andPurchaseCodeLike("%" + purchaseCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        return purchaseOrderMapper.selectByExample(example);
    }

    @Override
    public long findPurchaseOrderRowNum(String purchaseCode, String orderCode, String sampleCode, Integer status) {
        PurchaseOrderExample example = new PurchaseOrderExample();
        PurchaseOrderExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(purchaseCode)){
            criteria.andPurchaseCodeLike("%" + purchaseCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        return purchaseOrderMapper.countByExample(example);
    }

    @Transactional
    @Override
    public int insert(PurchaseOrderDetails purchaseOrderDetails, int loginId) {
        PurchaseOrder purchaseOrder = purchaseOrderDetails.getPurchaseOrder();
        purchaseOrder.setCreater(loginId);
        purchaseOrder.setCreateTime(new Date());
        purchaseOrder.setUser(userService.getUserName(loginId));
        if(purchaseOrderMapper.insertSelective(purchaseOrder) <= 0){
            LOGGER.info("保存采购单失败！");
            return 0;
        }
        List<PurchaseOrderDetail> list = purchaseOrderDetails.getDetailList();
        if(CollectionUtils.isEmpty(list)){
            return 1;
        }
        list.forEach(d -> d.setPurchaseOrderId(purchaseOrder.getId()));
        if(purchaseOrderDetailMapper.insertBatch(list, loginId) <= 0){
            LOGGER.info("保存采购单详情失败");
            throw new BusinessException("保存采购单详情失败");
        }
        return 1;
    }

    @Transactional
    @Override
    public int update(PurchaseOrderDetails purchaseOrderDetails, int loginId) {
        PurchaseOrder purchaseOrder = purchaseOrderDetails.getPurchaseOrder();
        if(Objects.isNull(purchaseOrder) || Objects.isNull(purchaseOrder.getId())){
            return 0;
        }
        PurchaseOrder purchaseOrderDb = this.getPurchaseOrderById(purchaseOrder.getId());
        if(Objects.isNull(purchaseOrderDb)){
            return 0;
        }
        BeanUtils.copyProperties(purchaseOrder, purchaseOrderDb);
        purchaseOrderDb.setUpdateTime(new Date());
        purchaseOrderDb.setModifier(loginId);
        if(purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrderDb) <= 0){
            LOGGER.info("修改采购单失败！");
            return 0;
        }
        PurchaseOrderDetailExample example = new PurchaseOrderDetailExample();
        PurchaseOrderDetailExample.Criteria criteria = example.createCriteria();
        criteria.andPurchaseOrderIdEqualTo(purchaseOrder.getId());
        purchaseOrderDetailMapper.deleteByExample(example);

        List<PurchaseOrderDetail> list = purchaseOrderDetails.getDetailList();
        if(CollectionUtils.isEmpty(list)){
            return 1;
        }
        list.forEach(d -> d.setPurchaseOrderId(purchaseOrder.getId()));
        if(purchaseOrderDetailMapper.insertBatch(list, loginId) <= 0){
            LOGGER.info("保存采购单详情失败");
            throw new BusinessException("保存采购单详情失败");
        }
        return 1;
    }

    @Override
    public int delete(int id, int loginId) {
        PurchaseOrder purchaseOrder = this.getPurchaseOrderById(id);
        if(Objects.isNull(purchaseOrder)){
            return 0;
        }
        if(PurchaseOrderStatus.AUDIT.getKey() == purchaseOrder.getStatus().intValue() ||
                PurchaseOrderStatus.FINISHED.getKey() == purchaseOrder.getStatus().intValue()){
            LOGGER.error("采购单【{}】状态【{}】不能删除！", id, purchaseOrder.getStatus());
            return -1;
        }
        purchaseOrder.setIsDeleted(DeletedEnum.YES.getKey());
        purchaseOrder.setUpdateTime(new Date());
        purchaseOrder.setModifier(loginId);
        return purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder);
    }

    @Override
    public int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus) {
        PurchaseOrder purchaseOrder = this.getPurchaseOrderById(id);
        if(Objects.isNull(purchaseOrder)){
            return 0;
        }
        if(!Stream.of(oldStatus).collect(Collectors.toList()).contains(purchaseOrder.getStatus().intValue())){
            LOGGER.error("采购单【{}】状态【{}】不正确，老状态【{}】！", id, purchaseOrder.getStatus(), oldStatus);
            return -1;
        }
        purchaseOrder.setStatus(newStatus);
        purchaseOrder.setUpdateTime(new Date());
        purchaseOrder.setModifier(loginId);
        if(newStatus == PurchaseOrderStatus.FINISHED.getKey()){
            purchaseOrder.setInboundDate(new Date());
            purchaseOrder.setWarehouser(userService.getUserName(loginId));
        }
        if(purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder) <= 0){
            return 0;
        }
        if (newStatus == PurchaseOrderStatus.AUDIT.getKey()) {
            messageService.insert("采购单入库", "您有一个新采购单将要入库，请及时处理！", MessageType.BUSINESS.getKey(), loginId, auditDept.getStorage());
        }
        if (newStatus == PurchaseOrderStatus.FINISHED.getKey()) {
            messageService.insert("采购单入库", "您的采购单已经入库，请及时查看！", MessageType.BUSINESS.getKey(), loginId, Arrays.asList(new Integer[]{purchaseOrder.getCreater()}));
        }
        if (newStatus == PurchaseOrderStatus.AUDIT.getKey()) {
            messageService.insert("采购单驳回", "您的采购单已被驳回，请及时处理！", MessageType.BUSINESS.getKey(), loginId, Arrays.asList(new Integer[]{purchaseOrder.getCreater()}));
        }
        return 1;
    }

    @Override
    public PurchaseOrder getPurchaseOrderById(int id) {
        return purchaseOrderMapper.selectByPrimaryKey(id);
    }
}
