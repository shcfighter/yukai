package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.OrderStatus;
import com.zero.common.enmu.PlanStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.PlanMapper;
import com.zero.model.Plan;
import com.zero.model.example.PlanExample;
import com.zero.service.IOrderService;
import com.zero.service.IPlanService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlanServiceImpl implements IPlanService {

    private static final Logger LOGGER = LogManager.getLogger(PlanServiceImpl.class);
    @Resource
    private PlanMapper planMapper;
    @Resource
    private IOrderService orderService;

    @Override
    public int insert(Plan plan, int loginId) {
        plan.setCreater(loginId);
        plan.setCreateTime(new Date());
        return planMapper.insertSelective(plan);
    }

    @Override
    public int update(Plan plan, int loginId) {
        Plan planDb = this.getPlanById(plan.getId());
        if(Objects.isNull(planDb)){
            return 0;
        }
        if(PlanStatus.SAVE.getKey() != plan.getStatus().intValue()){
            return -1;
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
        if(PlanStatus.SAVE.getKey() != plan.getStatus().intValue()){
            return -1;
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
    public List<Plan> findPlanPage(String productName, String batchNo, Integer status, Integer pageNum, Integer pageSize) {
        PlanExample example = new PlanExample();
        if(Objects.nonNull(pageNum) && Objects.nonNull(pageSize)){
            example.setPage(pageNum);
            example.setLimit(pageSize);
        }
        PlanExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(batchNo)){
            criteria.andBatchNoLike("%" + batchNo + "%");
        }
        return Optional.ofNullable(planMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }

    @Override
    public long findPlanRowNum(String productName, String batchNo, Integer status) {
        PlanExample example = new PlanExample();
        PlanExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(batchNo)){
            criteria.andBatchNoLike("%" + batchNo + "%");
        }
        return planMapper.countByExample(example);
    }

    @Transactional
    @Override
    public int updateToProduce (int id, int loginId) {
        Plan plan = this.getPlanById(id);
        if(Objects.isNull(plan)){
            return 0;
        }
        if(PlanStatus.SAVE.getKey() != plan.getStatus().intValue()){
            return -1;
        }
        plan.setStatus(PlanStatus.PRODUCE.getKey());
        plan.setUpdateTime(new Date());
        plan.setModifier(loginId);
        return planMapper.updateByPrimaryKeySelective(plan);
    }

    @Override
    public int updateToFinish(int id, int loginId) {
        Plan plan = this.getPlanById(id);
        if(Objects.isNull(plan)){
            return 0;
        }
        if(PlanStatus.PRODUCE.getKey() != plan.getStatus()){
            LOGGER.error("生产计划【{}】状态【{}】不正确", id, plan.getStatus());
            return -1;
        }
        plan.setStatus(PlanStatus.FINISHED.getKey());
        plan.setUpdateTime(new Date());
        plan.setModifier(loginId);
        return planMapper.updateByPrimaryKeySelective(plan);
    }
}
