package com.zero.service.impl;

import com.zero.common.enmu.DeletedEnum;
import com.zero.mapper.PlanMaterialMapper;
import com.zero.model.PlanMaterial;
import com.zero.model.example.PlanMaterialExample;
import com.zero.service.IPlanMaterialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PlanMaterialServiceImpl implements IPlanMaterialService {

    @Resource
    private PlanMaterialMapper planMaterialMapper;

    @Override
    public List<PlanMaterial> getPlanMaterialByOrderId(int planId) {
        PlanMaterialExample example = new PlanMaterialExample();
        PlanMaterialExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        criteria.andPlanIdEqualTo(planId);
        return planMaterialMapper.selectByExample(example);
    }
}
