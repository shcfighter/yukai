package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.MaterialOutBoundStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.MaterialOutboundMapper;
import com.zero.mapper.OutboundMaterialMapper;
import com.zero.model.MaterialOutbound;
import com.zero.model.OutboundMaterial;
import com.zero.model.example.OutboundMaterialExample;
import com.zero.model.example.MaterialOutboundExample;
import com.zero.model.verify.MaterialOutboundDetails;
import com.zero.service.IMaterialOutboundService;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MaterialOutboundServiceImpl implements IMaterialOutboundService {

    private static final Logger LOGGER = LogManager.getLogger(MaterialOutboundServiceImpl.class);

    @Resource
    private MaterialOutboundMapper materialOutboundMapper;
    @Resource
    private OutboundMaterialMapper outboundMaterialMapper;

    @Override
    public List<MaterialOutbound> findMaterialOutboundByPage(String materialName, String orderCode, String color, Integer status, Integer pageNum, Integer pageSize) {
        MaterialOutboundExample example = new MaterialOutboundExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        MaterialOutboundExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(materialName)){
            criteria.andMaterialNameLike("%" + materialName + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(color)){
            criteria.andColorLike("%" + color + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        return materialOutboundMapper.selectByExample(example);
    }

    @Override
    public long findMaterialOutboundRowNum(String materialName, String orderCode, String color, Integer status) {
        MaterialOutboundExample example = new MaterialOutboundExample();
        MaterialOutboundExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(StringUtils.isNotEmpty(materialName)){
            criteria.andMaterialNameLike("%" + materialName + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        if(StringUtils.isNotEmpty(color)){
            criteria.andColorLike("%" + color + "%");
        }
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        return materialOutboundMapper.countByExample(example);
    }

    @Transactional
    @Override
    public int insert(MaterialOutboundDetails materialOutboundDetails, int loginId) {
        MaterialOutbound materialOutbound = materialOutboundDetails.getMaterialOutbound();
        materialOutbound.setCreater(loginId);
        materialOutbound.setCreateTime(new Date());

        if(materialOutboundMapper.insertSelective(materialOutbound) <= 0){
            LOGGER.error("新增计划单失败！");
            return 0;
        }
        List<OutboundMaterial> materialList = materialOutboundDetails.getMaterialList();
        if(CollectionUtils.isEmpty(materialList)){
            return 1;
        }
        materialList.forEach(d -> d.setOutboundId(materialOutbound.getId()));
        outboundMaterialMapper.insertBatch(materialList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int update(MaterialOutboundDetails materialOutboundDetails, int loginId) {
        MaterialOutbound materialOutbound = materialOutboundDetails.getMaterialOutbound();
        if(Objects.isNull(materialOutbound) || Objects.isNull(materialOutbound.getId())){
            return 0;
        }
        MaterialOutbound materialOutboundDb = this.getMaterialOutboundById(materialOutbound.getId());
        if(Objects.isNull(materialOutboundDb)){
            return 0;
        }
        BeanUtils.copyProperties(materialOutbound, materialOutboundDb);
        materialOutboundDb.setUpdateTime(new Date());
        materialOutboundDb.setModifier(loginId);
        if(materialOutboundMapper.updateByPrimaryKeySelective(materialOutboundDb) <= 0){
            return 0;
        }

        OutboundMaterialExample outboundMaterialExample = new OutboundMaterialExample();
        OutboundMaterialExample.Criteria criteria2 = outboundMaterialExample.createCriteria();
        criteria2.andOutboundIdEqualTo(materialOutbound.getId());
        outboundMaterialMapper.deleteByExample(outboundMaterialExample);
        List<OutboundMaterial> materialList = materialOutboundDetails.getMaterialList();
        if(CollectionUtils.isEmpty(materialList)){
            return 1;
        }
        materialList.forEach(m -> m.setOutboundId(materialOutbound.getId()));
        outboundMaterialMapper.insertBatch(materialList, loginId);
        return 1;
    }

    @Override
    public int delete(int id, int loginId) {
        MaterialOutbound materialOutbound = this.getMaterialOutboundById(id);
        if(Objects.isNull(materialOutbound)){
            return 0;
        }
        if(MaterialOutBoundStatus.AUDIT.getKey() == materialOutbound.getStatus().intValue() ||
                MaterialOutBoundStatus.FINISHED.getKey() == materialOutbound.getStatus().intValue()){
            LOGGER.error("采购单【{}】状态【{}】不能删除！", id, materialOutbound.getStatus());
            return -1;
        }
        materialOutbound.setIsDeleted(DeletedEnum.YES.getKey());
        materialOutbound.setUpdateTime(new Date());
        materialOutbound.setModifier(loginId);
        return materialOutboundMapper.updateByPrimaryKeySelective(materialOutbound);
    }

    @Override
    public int updateStatus(int id, int newStatus, int loginId,  Integer ...oldStatus) {
        MaterialOutbound materialOutbound = this.getMaterialOutboundById(id);
        if(Objects.isNull(materialOutbound)){
            return 0;
        }
        if(!Stream.of(oldStatus).collect(Collectors.toList()).contains(materialOutbound.getStatus().intValue())){
            LOGGER.error("采购单【{}】状态【{}】不正确，老状态【{}】！", id, materialOutbound.getStatus(), oldStatus);
            return -1;
        }
        materialOutbound.setStatus(newStatus);
        materialOutbound.setUpdateTime(new Date());
        materialOutbound.setModifier(loginId);
        return materialOutboundMapper.updateByPrimaryKeySelective(materialOutbound);
    }

    @Override
    public MaterialOutbound getMaterialOutboundById(int id) {
        return materialOutboundMapper.selectByPrimaryKey(id);
    }
}
