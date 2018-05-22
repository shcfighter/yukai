package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.PlanMapper;
import com.zero.model.Plan;
import com.zero.model.example.PlanExample;
import com.zero.service.IPlanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlanServiceImpl implements IPlanService {

    @Resource
    private PlanMapper planMapper;

    @Override
    public int insert(Plan plan, int loginId) {
        plan.setCreater(loginId);
        plan.setCreateTime(new Date());
        return planMapper.insert(plan);
    }

    @Override
    public int update(Plan plan, int loginId) {
        Plan planDb = this.getPlanById(plan.getId());
        if(Objects.isNull(planDb)){
            return 0;
        }
        BeanUtils.copyProperties(plan, planDb);
        planDb.setUpdateTime(new Date());
        planDb.setModifier(loginId);
        return planMapper.updateByPrimaryKeySelective(planDb);
    }

    @Override
    public int delete(int id, int loginId) {
        Plan plan = this.getPlanById(id);
        if(Objects.isNull(plan)){
            return 0;
        }
        plan.setIsDeleted(DeletedEnum.YES.getKey());
        plan.setUpdateTime(new Date());
        plan.setModifier(loginId);
        return planMapper.updateByPrimaryKeySelective(plan);
    }

    @Override
    public Plan getPlanById(int id) {
        return planMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Plan> findPlan(int orderId, String batchNo) {
        PlanExample example = new PlanExample();
        PlanExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        if(StringUtils.isNotEmpty(batchNo)){
            criteria.andBatchNoLike("%" + batchNo + "%");
        }
        return Optional.ofNullable(planMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }
}
