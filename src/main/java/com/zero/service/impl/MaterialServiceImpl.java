package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.SampleMaterialMapper;
import com.zero.model.SampleMaterial;
import com.zero.model.example.SampleMaterialExample;
import com.zero.service.IMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MaterialServiceImpl implements IMaterialService {

    @Resource
    private SampleMaterialMapper sampleMaterialMapper;

    @Override
    public SampleMaterial getMaterialById(int id) {
        return sampleMaterialMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delete(int id, int loginId) {
        SampleMaterial material = sampleMaterialMapper.selectByPrimaryKey(id);
        if(Objects.isNull(material)){
            return 0;
        }
        material.setIsDeleted(DeletedEnum.YES.getKey());
        material.setModifier(loginId);
        material.setUpdateTime(new Date());
        return sampleMaterialMapper.updateByPrimaryKeySelective(material);
    }

    @Override
    public int insert(SampleMaterial sampleMaterial, int loginId) {
        sampleMaterial.setCreater(loginId);
        sampleMaterial.setCreateTime(new Date());
        return sampleMaterialMapper.insertSelective(sampleMaterial);
    }

    @Override
    public int update(SampleMaterial sampleMaterial, int loginId) {
        SampleMaterial material = sampleMaterialMapper.selectByPrimaryKey(sampleMaterial.getId());
        if(Objects.isNull(material)){
            return 0;
        }
        BeanUtils.copyProperties(sampleMaterial, material);
        material.setModifier(loginId);
        material.setUpdateTime(new Date());
        return sampleMaterialMapper.updateByPrimaryKeySelective(material);
    }

    @Override
    public int insertBatch(List<SampleMaterial> list, int loginId) {
        if(CollectionUtils.isEmpty(list)){
            return 0;
        }
        return sampleMaterialMapper.insertBatch(list, loginId);
    }

    @Override
    public List<SampleMaterial> findMaterial(int sampleId) {
        SampleMaterialExample example = new SampleMaterialExample();
        SampleMaterialExample.Criteria criteria = example.createCriteria();
        criteria.andSimpleIdEqualTo(sampleId);
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        return Optional.ofNullable(sampleMaterialMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }
}
