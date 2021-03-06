package com.zero.service.impl;

import com.zero.mapper.GoodsUseRecordMapper;
import com.zero.model.GoodsUseRecord;
import com.zero.model.example.GoodsUseRecordExample;
import com.zero.service.IGoodsRecordService;
import com.zero.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class GoodsRecordServiceImpl implements IGoodsRecordService {
    @Resource
    private GoodsUseRecordMapper goodsUseRecordMapper;
    @Resource
    private IUserService userService;

    @Override
    public int insert(GoodsUseRecord record, int loginId, String loginName) {
        record.setCreater(loginId);
        record.setUser(loginName);
        record.setCreateTime(new Date());
        return goodsUseRecordMapper.insertSelective(record);
    }

    @Override
    public int insert(int goodsId, String goodsName, String goodsModel, String batchNo, int goodsNum, int originalNum, int type, int loginId, int user) {
        GoodsUseRecord record = new GoodsUseRecord();
        record.setOriginalNum(originalNum);
        record.setType(type);
        record.setBatchNo(batchNo);
        record.setGoodsId(goodsId);
        record.setGoodsName(goodsName);
        record.setGoodsModel(goodsModel);
        record.setGoodsNum(goodsNum);
        record.setCreater(loginId);
        record.setUser(userService.getUserName(user));
        record.setCreateTime(new Date());
        return goodsUseRecordMapper.insertSelective(record);
    }

    @Override
    public List<GoodsUseRecord> findGoodsRecordPage(String goodsName, String goodsModel, String batchNo, String user, int pageNum, int pageSize) {
        GoodsUseRecordExample example = new GoodsUseRecordExample();
        example.setLimit(pageNum);
        example.setPage(pageSize);
        example.setOrderByClause(" create_time desc");
        GoodsUseRecordExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsNameLike("%" + goodsModel + "%");
        }
        if(StringUtils.isNotEmpty(batchNo)){
            criteria.andBatchNoLike("%" + batchNo + "%");
        }
        if(StringUtils.isNotEmpty(user)){
            criteria.andUserLike("%" + user + "%");
        }
        return goodsUseRecordMapper.selectByExample(example);
    }

    @Override
    public long findRowNum(String goodsName, String goodsModel, String batchNo, String user) {
        GoodsUseRecordExample example = new GoodsUseRecordExample();
        GoodsUseRecordExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(goodsName)){
            criteria.andGoodsNameLike("%" + goodsName + "%");
        }
        if(StringUtils.isNotEmpty(goodsModel)){
            criteria.andGoodsNameLike("%" + goodsModel + "%");
        }
        if(StringUtils.isNotEmpty(batchNo)){
            criteria.andBatchNoLike("%" + batchNo + "%");
        }
        if(StringUtils.isNotEmpty(user)){
            criteria.andUserLike("%" + user + "%");
        }
        return goodsUseRecordMapper.countByExample(example);
    }
}
