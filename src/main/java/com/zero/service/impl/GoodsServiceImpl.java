package com.zero.service.impl;

import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.OutBoundStatus;
import com.zero.common.enmu.PurchaseOrderStatus;
import com.zero.common.enmu.RecordType;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.GoodsMapper;
import com.zero.model.Goods;
import com.zero.model.GoodsUseRecord;
import com.zero.model.OutBound;
import com.zero.model.PurchaseOrder;
import com.zero.model.example.GoodsExample;
import com.zero.service.IGoodsRecordService;
import com.zero.service.IGoodsService;
import com.zero.service.IOutBoundService;
import com.zero.service.IPurchaseOrderService;
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
public class GoodsServiceImpl implements IGoodsService {

    private static final Logger LOGGER = LogManager.getLogger(GoodsServiceImpl.class);
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private IGoodsRecordService goodsRecordService;
    @Resource
    private IPurchaseOrderService purchaseOrderService;
    @Resource
    private IOutBoundService outBoundService;


    @Override
    public List<Goods> findGoodsPage(String goodsName, String goodsModel, String batchNo, Integer type, Integer pageNum, Integer pageSize) {
        GoodsExample example = new GoodsExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setLimit(pageSize);
            example.setPage(pageNum);
        }
        GoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsNameLike("%" + goodsModel + "%");
        }

        if(Objects.nonNull(type)){
            criteria.andTypeEqualTo(type);
        }
        return goodsMapper.selectByExample(example);
    }

    @Override
    public long findRowNum(String goodsName, String goodsModel, String batchNo, Integer type) {
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsNameLike("%" + goodsModel + "%");
        }
        if(Objects.nonNull(type)){
            criteria.andTypeEqualTo(type);
        }
        return goodsMapper.countByExample(example);
    }

    @Transactional
    @Override
    public int insert(Goods goods, int loginId, String loginName) {
        goods.setCreateTime(new Date());
        goods.setCreater(loginId);
        GoodsUseRecord record = new GoodsUseRecord();
        int rowNum = goodsMapper.insertSelective(goods);
        if(rowNum <= 0){
            return 0;
        }
        record.setType(RecordType.SAVE.getKey());
        record.setGoodsId(goods.getId());
        record.setGoodsName(goods.getGoodsName());
        record.setGoodsModel(goods.getGoodsModel());
        record.setGoodsNum(goods.getNum());
        record.setCreateTime(new Date());
        record.setCreater(loginId);
        int num = goodsRecordService.insert(record, loginId, loginName);
        if(num <= 0){
            throw new BusinessException("物品记录插入异常，回滚");
        }
        return 1;
    }

    @Transactional
    @Override
    public int update(Goods goods, int loginId, String loginName) {
        Goods goodsDb = this.getGoodsById(goods.getId());
        if(Objects.isNull(goodsDb)){
            return 0;
        }
        GoodsUseRecord record = new GoodsUseRecord();
        record.setOriginalNum(goodsDb.getNum());
        BeanUtils.copyProperties(goods, goodsDb);
        goodsDb.setModifier(loginId);
        goodsDb.setUpdateTime(new Date());
        int rowNum = goodsMapper.updateByPrimaryKeySelective(goodsDb);
        if(rowNum <= 0){
            return 0;
        }
        record.setType(RecordType.UPDATE.getKey());
        record.setGoodsId(goods.getId());
        record.setGoodsName(goods.getGoodsName());
        record.setGoodsModel(goods.getGoodsModel());
        record.setGoodsNum(goods.getNum());
        record.setCreateTime(new Date());
        record.setCreater(loginId);
        int num = goodsRecordService.insert(record, loginId, loginName);
        if(num <= 0){
            throw new BusinessException("物品记录修改异常，回滚");
        }
        return 1;
    }

    @Override
    public Goods getGoodsById(int id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteGoodsById(int id, int loginId, String loginName) {
        Goods goods = this.getGoodsById(id);
        if(Objects.isNull(goods)){
            return 0;
        }
        goods.setIsDeleted(DeletedEnum.YES.getKey());
        goods.setModifier(loginId);
        goods.setUpdateTime(new Date());
        return goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Transactional
    @Override
    public int inbound(int purchaseOrderId, int loginId, String loginName) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
        if(Objects.isNull(purchaseOrder)){
            return 0;
        }
        final String model = purchaseOrder.getGoodsModel();
        List<Goods> list = this.findGoodsList(purchaseOrder.getGoodsName(), null, purchaseOrder.getType());
        if (CollectionUtils.isEmpty(list)){
            Goods goods = new Goods();
            goods.setGoodsName(purchaseOrder.getGoodsName());
            goods.setGoodsModel(model);
            goods.setColor(purchaseOrder.getColor());
            goods.setNum(purchaseOrder.getNum());
            goods.setType(purchaseOrder.getType());
            goods.setUnit(purchaseOrder.getUnit());
            goods.setCreateTime(new Date());
            if(goodsMapper.insertSelective(goods) <= 0){
                throw new BusinessException("物品记录新增异常，回滚");
            }
            if(purchaseOrderService.updateStatus(purchaseOrderId, PurchaseOrderStatus.FINISHED.getKey(), loginId, PurchaseOrderStatus.AUDIT.getKey()) <= 0){
                throw new BusinessException("修改采购单失败，回滚");
            }
            int goodsNum = goodsRecordService.insert(goods.getId(), goods.getGoodsName(), goods.getGoodsModel(), purchaseOrderId + "", goods.getNum(),
                    0, RecordType.INBOUND.getKey(), loginId, purchaseOrder.getCreater());
            if(goodsNum <= 0){
                throw new BusinessException("插入物品记录异常，回滚");
            }
            return 1;
        }
        Goods goods = null;
        for (Goods tmp: list){
            if(StringUtils.equals(tmp.getGoodsModel(), purchaseOrder.getGoodsModel())){
                goods = tmp;
                break;
            }
        }
        if(Objects.isNull(goods)){
            goods = list.get(0);
        }
        int originalNum = goods.getNum();
        goods.setNum(goods.getNum() + purchaseOrder.getNum());
        goods.setUpdateTime(new Date());
        if(goodsMapper.updateByPrimaryKeySelective(goods) <= 0){
            throw new BusinessException("物品记录修改异常，回滚");
        }
        if(purchaseOrderService.updateStatus(purchaseOrderId, PurchaseOrderStatus.FINISHED.getKey(), loginId, PurchaseOrderStatus.AUDIT.getKey()) <= 0){
            throw new BusinessException("修改采购单失败，回滚");
        }
        int goodsNum = goodsRecordService.insert(goods.getId(), goods.getGoodsName(), goods.getGoodsModel(), purchaseOrderId + "", goods.getNum(),
                originalNum, RecordType.INBOUND.getKey(), loginId, purchaseOrder.getCreater());
        if(goodsNum <= 0){
            throw new BusinessException("插入物品记录异常，回滚");
        }
        return 1;
    }

    @Transactional
    @Override
    public int outbound(int outboundid, int loginId, String loginName) {
        OutBound outBound = outBoundService.getOutBoundById(outboundid);
        if(Objects.isNull(outBound) || Objects.isNull(outBound.getGoodId())){
            return 0;
        }
        Goods goods = goodsMapper.selectByPrimaryKey(outBound.getGoodId());
        if(Objects.isNull(goods)){
            return 0;
        }
        if(outBound.getNum() > goods.getNum()){
            LOGGER.error("库存【{}】不足！", goods.getNum());
            return -1;
        }
        int originalNum = goods.getNum();
        goods.setModifier(loginId);
        goods.setUpdateTime(new Date());
        goods.setNum(goods.getNum() - outBound.getNum());
        int rowNum = goodsMapper.updateByPrimaryKeySelective(goods);
        if(rowNum <= 0){
            return 0;
        }
        int outNum = outBoundService.updateStatus(outboundid, loginId, OutBoundStatus.FINISHED.getKey(), OutBoundStatus.AUDIT.getKey());
        if(outNum <= 0){
            throw new BusinessException("修改库存数量异常，回滚");
        }
        int goodsNum = goodsRecordService.insert(goods.getId(), goods.getGoodsName(), goods.getGoodsModel(), outboundid + "", goods.getNum(),
                originalNum, RecordType.OUTBOUND.getKey(), loginId, outBound.getCreater());
        if(goodsNum <= 0){
            throw new BusinessException("插入物品记录异常，回滚");
        }
        return 1;
    }

    @Override
    public List<Goods> findGoodsList(String goodsName, String goodsModel, Integer type) {
        GoodsExample example = new GoodsExample();
        GoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsNameLike("%" + goodsModel + "%");
        }
        if(Objects.nonNull(type)){
            criteria.andTypeEqualTo(type);
        }
        return goodsMapper.selectByExample(example);
    }

}
