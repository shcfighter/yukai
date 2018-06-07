package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.PlanDetailMapper;
import com.zero.model.PlanDetail;
import com.zero.model.example.PlanDetailExample;
import com.zero.service.IPlanDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PlanDetailServiceImpl implements IPlanDetailService {

    @Resource
    private PlanDetailMapper planDetailMapper;

    @Override
    public List<PlanDetail> getPlanDetailByOrderId(int planId) {
        PlanDetailExample example = new PlanDetailExample();
        PlanDetailExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andPlanIdEqualTo(planId);
        return planDetailMapper.selectByExample(example);
    }
}
