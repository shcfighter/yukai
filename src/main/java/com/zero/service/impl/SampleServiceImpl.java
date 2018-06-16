package com.zero.service.impl;

import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.SampleStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.SampleDetailMapper;
import com.zero.mapper.SampleMapper;
import com.zero.model.Sample;
import com.zero.model.SampleDetail;
import com.zero.model.example.SampleDetailExample;
import com.zero.model.example.SampleExample;
import com.zero.service.ISampleService;
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
public class SampleServiceImpl implements ISampleService {

    private static final Logger LOGGER = LogManager.getLogger(SampleServiceImpl.class);

    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private SampleDetailMapper sampleDetailMapper;

    @Transactional
    @Override
    public int insertOrUpdate(Sample sample, List<SampleDetail> list, int loginId) {
        if(Objects.isNull(sample.getId())){
            sample.setCreater(loginId);
            sample.setCreateTime(new Date());
            int num = sampleMapper.insertSelective(sample);
            if(num <= 0){
                throw new BusinessException("保存样品失败！");
            }
            LOGGER.info("sample.getId():{}", sample.getId());
            list.stream().forEach(m -> {m.setSampleId(sample.getId());});
            if(!CollectionUtils.isEmpty(list)){
                sampleDetailMapper.insertBatch(list, loginId);
            }
            return 1;
        }
        Sample sampleDb = sampleMapper.selectByPrimaryKey(sample.getId());
        if(Objects.isNull(sampleDb)){
            return 0;
        }
        if(!sampleDb.getStatus().equals(SampleStatus.NEW.getKey())){
            LOGGER.error("样品【{}】已经进入计划科无法进行更改！", sampleDb.getId());
            return -1;
        }

        BeanUtils.copyProperties(sample, sampleDb);
        sampleDb.setModifier(loginId);
        sampleDb.setUpdateTime(new Date());

        SampleDetailExample example = new SampleDetailExample();
        SampleDetailExample.Criteria criteria = example.createCriteria();
        criteria.andSampleIdEqualTo(sampleDb.getId());
        sampleDetailMapper.deleteByExample(example);

        list.stream().forEach(m -> {m.setSampleId(sampleDb.getId());});
        if(!CollectionUtils.isEmpty(list)){
            sampleDetailMapper.insertBatch(list, loginId);
        }
        return sampleMapper.updateByPrimaryKeySelective(sampleDb);
    }

    @Override
    public List<Sample> findSamplePage(String style, String sampleCode, Integer status, Integer page, Integer pageSize) {
        SampleExample example = new SampleExample();
        example.setOrderByClause(" create_time desc");
        if(Objects.nonNull(page) && Objects.nonNull(pageSize)){
            example.setPage(page);
            example.setLimit(pageSize);
        }
        SampleExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(style)){
            criteria.andStyleLike("%" + style + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        return sampleMapper.selectByExample(example);
    }

    @Override
    public long findSampleRowNum(String style, String sampleCode, Integer status) {
        SampleExample example = new SampleExample();
        SampleExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if(Objects.nonNull(status)){
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(style)){
            criteria.andStyleLike("%" + style + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        return sampleMapper.countByExample(example);
    }

    @Override
    public Sample findSampleById(int id) {
        return sampleMapper.selectByPrimaryKey(id);
    }

    @Override
    public int deleteSample(int id, int loginId) {
        Sample sample = this.findSampleById(id);
        if (Objects.isNull(sample)) {
            return 0;
        }
        sample.setIsDeleted(DeletedEnum.YES.getKey());
        sample.setModifier(loginId);
        sample.setUpdateTime(new Date());
        return sampleMapper.updateByPrimaryKey(sample);
    }

    @Override
    public int updateToFinished(int id, int newStatus, int oldStatus, int loginId) {
        Sample sample = this.findSampleById(id);
        if(Objects.isNull(sample)){
            LOGGER.info("样品【{}】信息不存在!", id);
            return 0;
        }
        if(sample.getStatus().intValue() != oldStatus){
            LOGGER.info("样品【{}】状态【{}】不正确", id, sample.getStatus());
            return 0;
        }
        sample.setStatus(SampleStatus.FINISHED.getKey());
        sample.setUpdateTime(new Date());
        sample.setModifier(loginId);
        return sampleMapper.updateByPrimaryKeySelective(sample);
    }

    @Override
    public boolean isExsitSampleCode(String sampleCode, Integer id) {
        SampleExample example = new SampleExample();
        SampleExample.Criteria criteria = example.createCriteria();
        criteria.andSampleCodeEqualTo(sampleCode);
        if(Objects.nonNull(id)){
            criteria.andIdNotEqualTo(id);
        }
        return sampleMapper.countByExample(example) > 0 ? true : false;
    }
}
