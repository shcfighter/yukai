package com.zero.service.impl;

import com.google.common.collect.Maps;
import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.MaterialOutBoundStatus;
import com.zero.common.enmu.PurchaseOrderStatus;
import com.zero.common.enmu.RecordType;
import com.zero.mapper.MaterialMapper;
import com.zero.model.*;
import com.zero.model.example.MaterialExample;
import com.zero.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MaterialServiceImpl implements IMaterialService {

    private static final Logger LOGGER = LogManager.getLogger(MaterialServiceImpl.class);
    @Resource
    private MaterialMapper materialMapper;
    @Resource
    private IGoodsRecordService goodsRecordService;
    @Resource
    private IPurchaseOrderService purchaseOrderService;
    @Resource
    private IPurchaseOrderDetailService purchaseOrderDetailService;
    @Resource
    private IMaterialOutboundService materialOutboundService;
    @Resource
    private IOutboundMaterialService outboundMaterialService;

    @Override
    public List<Material> findMaterialPage(String productName, String color, String ingredients, Integer pageNum, Integer pageSize) {
        MaterialExample example = new MaterialExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setLimit(pageSize);
            example.setPage(pageNum);
        }
        MaterialExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(color)){
            criteria.andColorLike("%" + color + "%");
        }
        if(StringUtils.isNotEmpty(ingredients)){
            criteria.andIngredientsLike("%" + ingredients + "%");
        }
        return materialMapper.selectByExample(example);
    }

    @Override
    public long findRowNum(String productName, String color, String ingredients) {
        MaterialExample example = new MaterialExample();
        MaterialExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(color)){
            criteria.andColorLike("%" + color + "%");
        }
        if(StringUtils.isNotEmpty(ingredients)){
            criteria.andIngredientsLike("%" + ingredients + "%");
        }
        return materialMapper.countByExample(example);
    }

    @Override
    public Material getMaterialById(int id) {
        return materialMapper.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public int inbound(int purchaseOrderId, int loginId, String loginName) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
        List<PurchaseOrderDetail> purchaseOrderDetailList = purchaseOrderDetailService.getPurchaseOrderDetailByPurchaseId(purchaseOrderId);
        if(Objects.isNull(purchaseOrder) || CollectionUtils.isEmpty(purchaseOrderDetailList)){
            LOGGER.info("采购单【{}】不存在!", purchaseOrderId);
            return 0;
        }
        purchaseOrderDetailList.forEach(purchaseOrderDetail -> {
            List<Material> list = this.findMaterialPage(purchaseOrderDetail.getMaterialName(), purchaseOrderDetail.getColor(), null, null, null);
            if (CollectionUtils.isEmpty(list)){
                Material material = new Material();
                material.setProductName(purchaseOrderDetail.getMaterialName());
                material.setColor(purchaseOrderDetail.getColor());
                material.setNum(purchaseOrderDetail.getNum());
                material.setIngredients(purchaseOrderDetail.getIngredients());
                material.setWeight(purchaseOrderDetail.getWeight());
                material.setCreateTime(new Date());
                if(materialMapper.insertSelective(material) <= 0){
                    LOGGER.info("插入原材料记录异常，");
                    throw new BusinessException("原材料记录新增异常");
                }
            } else {
                Material material = list.get(0);
                material.setNum(material.getNum() + purchaseOrderDetail.getNum());
                material.setWeight(material.getWeight() + purchaseOrderDetail.getWeight());
                material.setUpdateTime(new Date());
                if(materialMapper.updateByPrimaryKeySelective(material) <= 0){
                    LOGGER.info("修改原材料【{}】数量异常", material.getId());
                    throw new BusinessException("修改原材料记录异常");
                }
            }
        });

        if(purchaseOrderService.updateStatus(purchaseOrderId, PurchaseOrderStatus.FINISHED.getKey(), loginId, PurchaseOrderStatus.AUDIT.getKey()) <= 0){
            LOGGER.info("更新采购单【{}】异常", purchaseOrderId);
            throw new BusinessException("修改采购单状态失败");
        }
        return 1;
    }

    @Transactional
    @Override
    public Map<String, Object> outbound(int outboundId, int loginId, String loginName) {
        Map<String, Object> result = Maps.newHashMap();
        MaterialOutbound materialOutbound = materialOutboundService.getMaterialOutboundById(outboundId);
        if(Objects.isNull(materialOutbound)){
            LOGGER.info("原材料申请单【{}】不存在!", outboundId);
            result.put("message", "原材料申请单不存在!");
            result.put("success", 0);
            return result;
        }
        List<OutboundMaterial> outboundMaterialList = outboundMaterialService.getOutboundMaterialByOutboundId(outboundId);
        if(CollectionUtils.isEmpty(outboundMaterialList)){
            LOGGER.info("原材料申请详情【{}】不存在!", outboundId);
            result.put("message", "原材料申请详情不存在!");
            result.put("success", 0);
            return result;
        }
        for (OutboundMaterial m : outboundMaterialList) {
            List<Material> materialList = this.findMaterialPage(m.getMaterialName(), m.getColor(), null, null, null);
            if(CollectionUtils.isEmpty(materialList)){
                LOGGER.info("{}的{}库存里不存在!", m.getColor(), m.getMaterialName());
                throw new BusinessException(m.getColor() + m.getMaterialName() + "库存里不存在！");
            }
            Material material = materialList.get(0);
            if(material.getNum() < m.getNum()){
                LOGGER.info("{}的{}库存不足!", m.getColor(), m.getMaterialName());
                throw new BusinessException(m.getColor() + m.getMaterialName() + "库存不足！");
            }
            int originalNum = material.getNum();
            material.setModifier(loginId);
            material.setUpdateTime(new Date());
            material.setNum(material.getNum() - m.getNum());
            int rowNum = materialMapper.updateByPrimaryKeySelective(material);
            if(rowNum <= 0){
                LOGGER.info("更新库存【{}】数量失败！", material.getId());
                result.put("message", "更新库存数量失败！");
                result.put("success", 0);
                return result;
            }
            goodsRecordService.insert(material.getId(), "原材料：" + material.getProductName(), material.getColor(), outboundId + "", material.getNum(),
                    originalNum, RecordType.OUTBOUND.getKey(), loginId, materialOutbound.getCreater());
        }
        int outNum = materialOutboundService.updateStatus(outboundId, MaterialOutBoundStatus.FINISHED.getKey(), loginId, MaterialOutBoundStatus.AUDIT.getKey());
        if(outNum <= 0){
            LOGGER.info("更新原材料申请单状态【{}】数量失败！", outboundId);
            throw new BusinessException("更新原材料申请单状态异常");
        }
        result.put("message", "原材料出库成功");
        result.put("success", 1);
        return result;
    }

    @Override
    public long selectTotal() {
        return materialMapper.selectTotal();
    }
}
