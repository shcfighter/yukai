package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.OutBoundStatus;
import com.zero.common.enmu.OutBoundType;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.OutBoundMapper;
import com.zero.model.OutBound;
import com.zero.model.example.OutBoundExample;
import com.zero.service.IOutBoundService;
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
public class OutBoundServiceImpl implements IOutBoundService {
    private static final Logger LOGGER = LogManager.getLogger(OutBoundServiceImpl.class);

    @Resource
    private OutBoundMapper outBoundMapper;

    @Override
    public List<OutBound> findOutBoundByPage(String goodsName, String goodsModel, Integer status, OutBoundType type, Integer pageNum, Integer pageSize) {
        OutBoundExample example = new OutBoundExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        OutBoundExample.Criteria criteria = example.createCriteria();
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
        if(Objects.nonNull(type)){
            criteria.andOutboundTypeEqualTo(type.getKey());
        }
        return outBoundMapper.selectByExample(example);
    }

    @Override
    public long findOutBoundRowNum(String goodsName, String goodsModel, Integer status, OutBoundType type) {
        OutBoundExample example = new OutBoundExample();
        OutBoundExample.Criteria criteria = example.createCriteria();
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
        if(Objects.nonNull(type)){
            criteria.andOutboundTypeEqualTo(type.getKey());
        }
        return outBoundMapper.countByExample(example);
    }

    @Override
    public int insert(OutBound outBound, int loginId) {
        outBound.setCreater(loginId);
        outBound.setCreateTime(new Date());
        return outBoundMapper.insertSelective(outBound);
    }

    @Override
    public int update(OutBound outBound, int loginId) {
        if(Objects.isNull(outBound) || Objects.isNull(outBound.getId())){
            return 0;
        }
        OutBound outBoundDb = this.getOutBoundById(outBound.getId());
        if(Objects.isNull(outBoundDb)){
            return 0;
        }
        BeanUtils.copyProperties(outBound, outBoundDb);
        outBoundDb.setUpdateTime(new Date());
        outBoundDb.setModifier(loginId);
        return outBoundMapper.updateByPrimaryKeySelective(outBoundDb);
    }

    @Override
    public int delete(int id, int loginId) {
        OutBound outBound  = this.getOutBoundById(id);
        if(Objects.isNull(outBound)){
            return 0;
        }
        if(OutBoundStatus.AUDIT.getKey() == outBound.getStatus().intValue() ||
                OutBoundStatus.FINISHED.getKey() == outBound.getStatus().intValue()){
            LOGGER.error("出库申请单【{}】状态【{}】不能删除！", id, outBound.getStatus());
            return -1;
        }
        outBound.setIsDeleted(DeletedEnum.YES.getKey());
        outBound.setUpdateTime(new Date());
        outBound.setModifier(loginId);
        return outBoundMapper.updateByPrimaryKey(outBound);
    }

    @Override
    public int updateStatus(int id, int loginId, int newStatus, Integer... oldStatus) {
        OutBound outBound = this.getOutBoundById(id);
        if(Objects.isNull(outBound)){
            return 0;
        }
        if(!Stream.of(oldStatus).collect(Collectors.toList()).contains(outBound.getStatus().intValue())){
            LOGGER.error("出库申请单【{}】状态【{}】不正确，老状态【{}】！", id, outBound.getStatus(), oldStatus);
            return -1;
        }
        outBound.setStatus(newStatus);
        outBound.setUpdateTime(new Date());
        outBound.setModifier(loginId);
        return outBoundMapper.updateByPrimaryKey(outBound);
    }

    @Override
    public OutBound getOutBoundById(int id) {
        return outBoundMapper.selectByPrimaryKey(id);
    }
}
