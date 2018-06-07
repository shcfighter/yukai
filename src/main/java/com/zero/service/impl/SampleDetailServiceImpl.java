package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.SampleDetailMapper;
import com.zero.model.SampleDetail;
import com.zero.model.example.SampleDetailExample;
import com.zero.service.ISampleDetailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class SampleDetailServiceImpl implements ISampleDetailService {

    private static final Logger LOGGER = LogManager.getLogger(SampleDetailServiceImpl.class);

    @Resource
    private SampleDetailMapper sampleDetailMapper;

    @Override
    public List<SampleDetail> findSampleDetail(int sampleId) {
        SampleDetailExample example = new SampleDetailExample();
        SampleDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andSampleIdEqualTo(sampleId);
        return sampleDetailMapper.selectByExample(example);
    }

    @Override
    public int insertSampleDetail(SampleDetail detail, int loginId) {
        detail.setCreater(loginId);
        detail.setCreateTime(new Date());
        detail.setIsDeleted(DeletedEnum.NO.getKey());
        return sampleDetailMapper.insertSelective(detail);
    }

    @Override
    public int deleteSampleDetail(int id, int loginId) {
        SampleDetail sampleDetail = this.getSampleDetailById(id);
        if(Objects.isNull(sampleDetail)){
            LOGGER.error("样品尺寸详情【{}】不存在！", id);
            return 0;
        }
        sampleDetail.setIsDeleted(DeletedEnum.YES.getKey());
        sampleDetail.setUpdateTime(new Date());
        sampleDetail.setModifier(loginId);
        return sampleDetailMapper.updateByPrimaryKeySelective(sampleDetail);
    }

    @Override
    public SampleDetail getSampleDetailById(int id) {
        return sampleDetailMapper.selectByPrimaryKey(id);
    }
}
