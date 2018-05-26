package com.zero.service.impl;

import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.RecordType;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.GoodsMapper;
import com.zero.model.Goods;
import com.zero.model.GoodsUseRecord;
import com.zero.model.example.GoodsExample;
import com.zero.service.IGoodsRecordService;
import com.zero.service.IGoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private IGoodsRecordService goodsRecordService;

    @Override
    public List<Goods> findGoodsPage(String goodsName, String goodsModel, String batchNo, Integer type, Integer pageNum, Integer pageSize) {
        GoodsExample example = new GoodsExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setLimit(pageNum);
            example.setPage(pageSize);
        }
        GoodsExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsNameLike("%" + goodsModel + "%");
        }
        if(StringUtils.isNotEmpty(batchNo)){
            criteria.andBatchNoLike("%" + batchNo + "%");
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
        if(StringUtils.isNotEmpty(batchNo)){
            criteria.andBatchNoLike("%" + batchNo + "%");
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
        record.setBatchNo(goods.getBatchNo());
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
        record.setBatchNo(goods.getBatchNo());
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
    public int inbound(int id, int num, int loginId, String loginName) {
        Goods goodsDb = this.getGoodsById(id);
        if(Objects.isNull(goodsDb)){
            return 0;
        }
        GoodsUseRecord record = new GoodsUseRecord();
        record.setOriginalNum(goodsDb.getNum());
        goodsDb.setModifier(loginId);
        goodsDb.setUpdateTime(new Date());
        goodsDb.setNum(goodsDb.getNum() + num);
        int rowNum = goodsMapper.updateByPrimaryKeySelective(goodsDb);
        if(rowNum <= 0){
            return 0;
        }
        record.setType(RecordType.INBOUND.getKey());
        record.setGoodsId(goodsDb.getId());
        record.setGoodsName(goodsDb.getGoodsName());
        record.setGoodsModel(goodsDb.getGoodsModel());
        record.setBatchNo(goodsDb.getBatchNo());
        record.setGoodsNum(goodsDb.getNum());
        record.setCreateTime(new Date());
        record.setCreater(loginId);
        int goodsNum = goodsRecordService.insert(record, loginId, loginName);
        if(goodsNum <= 0){
            throw new BusinessException("物品记录修改异常，回滚");
        }
        return 1;
    }

    @Transactional
    @Override
    public int outbound(int id, int num, int loginId, String loginName) {
        Goods goodsDb = this.getGoodsById(id);
        if(Objects.isNull(goodsDb)){
            return 0;
        }
        GoodsUseRecord record = new GoodsUseRecord();
        record.setOriginalNum(goodsDb.getNum());
        goodsDb.setModifier(loginId);
        goodsDb.setUpdateTime(new Date());
        if(goodsDb.getNum() < num){
            return 0;
        }
        goodsDb.setNum(goodsDb.getNum() - num);
        int rowNum = goodsMapper.updateByPrimaryKeySelective(goodsDb);
        if(rowNum <= 0){
            return 0;
        }
        record.setType(RecordType.OUTBOUND.getKey());
        record.setGoodsId(goodsDb.getId());
        record.setGoodsName(goodsDb.getGoodsName());
        record.setGoodsModel(goodsDb.getGoodsModel());
        record.setBatchNo(goodsDb.getBatchNo());
        record.setGoodsNum(goodsDb.getNum());
        record.setCreateTime(new Date());
        record.setCreater(loginId);
        int goodsNum = goodsRecordService.insert(record, loginId, loginName);
        if(goodsNum <= 0){
            throw new BusinessException("物品记录修改异常，回滚");
        }
        return 1;
    }

}
