package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.GoodsGoodsType;
import com.zero.common.enmu.PurchaseOrderStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.PurchaseOrderMapper;
import com.zero.model.PurchaseOrder;
import com.zero.model.example.PurchaseOrderExample;
import com.zero.service.IPurchaseOrderService;
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
public class PurchaseOrderServiceImpl implements IPurchaseOrderService {

    private static final Logger LOGGER = LogManager.getLogger(PurchaseOrderServiceImpl.class);

    @Resource
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public List<PurchaseOrder> findPurchaseOrderByPage(String goodsName, String goodsModel, Integer status, GoodsGoodsType goodsType, Integer pageNum, Integer pageSize) {
        PurchaseOrderExample example = new PurchaseOrderExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        PurchaseOrderExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsModelLike("%" + goodsModel + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        if(Objects.nonNull(goodsType)){
            criteria.andGoodsTypeEqualTo(goodsType.getKey());
        }
        return purchaseOrderMapper.selectByExample(example);
    }

    @Override
    public long findPurchaseOrderRowNum(String goodsName, String goodsModel, Integer status, GoodsGoodsType goodsType) {
        PurchaseOrderExample example = new PurchaseOrderExample();
        PurchaseOrderExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsModelLike("%" + goodsModel + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        if(Objects.nonNull(goodsType)){
            criteria.andGoodsTypeEqualTo(goodsType.getKey());
        }
        return purchaseOrderMapper.countByExample(example);
    }

    @Override
    public int insert(PurchaseOrder purchaseOrder, int loginId) {
        purchaseOrder.setCreater(loginId);
        purchaseOrder.setCreateTime(new Date());
        return purchaseOrderMapper.insertSelective(purchaseOrder);
    }

    @Override
    public int update(PurchaseOrder purchaseOrder, int loginId) {
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
        return purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrderDb);
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
        return purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder);
    }

    @Override
    public PurchaseOrder getPurchaseOrderById(int id) {
        return purchaseOrderMapper.selectByPrimaryKey(id);
    }
}