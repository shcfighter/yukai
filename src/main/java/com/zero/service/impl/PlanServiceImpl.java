package com.zero.service.impl;

import com.google.common.collect.Lists;
import com.zero.common.BusinessException;
import com.zero.common.enmu.DeletedEnum;
import com.zero.common.enmu.OrderStatus;
import com.zero.common.enmu.PlanStatus;
import com.zero.common.utils.BeanUtils;
import com.zero.mapper.PlanDetailMapper;
import com.zero.mapper.PlanMapper;
import com.zero.mapper.PlanMaterialMapper;
import com.zero.model.Plan;
import com.zero.model.PlanDetail;
import com.zero.model.PlanMaterial;
import com.zero.model.example.PlanDetailExample;
import com.zero.model.example.PlanExample;
import com.zero.model.example.PlanMaterialExample;
import com.zero.model.verify.PlanDetails;
import com.zero.service.IOrderService;
import com.zero.service.IPlanService;
import com.zero.service.IUserService;
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
    private PlanMaterialMapper planMaterialMapper;
    @Resource
    PlanDetailMapper planDetailMapper;
    @Resource
    private IUserService userService;
    @Resource
    private IOrderService orderService;

    @Transactional
    @Override
    public int insert(PlanDetails planDetails, int loginId) {
        Plan plan = planDetails.getPlan();
        plan.setCreater(loginId);
        plan.setStatus(PlanStatus.SAVE.getKey());
        plan.setBillingId(loginId);
        plan.setBillingUser(userService.getUserName(loginId));
        plan.setBillingDate(new Date());
        plan.setCreateTime(new Date());
        if(planMapper.insertSelective(plan) <= 0){
            LOGGER.error("新增计划单失败！");
            return 0;
        }
        List<PlanDetail> detailList = planDetails.getDetailList();
        detailList.forEach(d -> d.setPlanId(plan.getId()));
        planDetailMapper.insertBatch(detailList, loginId);

        List<PlanMaterial> materialList = planDetails.getMaterialList();
        materialList.forEach(m -> m.setPlanId(plan.getId()));
        planMaterialMapper.insertBatch(materialList, loginId);

        return 1;
    }

    @Transactional
    @Override
    public int update(PlanDetails planDetails, int loginId) {
        Plan plan = planDetails.getPlan();
        Plan planDb = this.getPlanById(plan.getId());
        if(Objects.isNull(planDb)){
            return 0;
        }
        if(PlanStatus.SAVE.getKey() != planDb.getStatus().intValue()){
            return -1;
        }
        BeanUtils.copyProperties(plan, planDb);
        planDb.setUpdateTime(new Date());
        planDb.setModifier(loginId);
        if(planMapper.updateByPrimaryKeySelective(planDb) <= 0){
            LOGGER.error("修改计划单失败！");
            return 0;
        }
        PlanDetailExample planDetailExample = new PlanDetailExample();
        PlanDetailExample.Criteria criteria = planDetailExample.createCriteria();
        criteria.andPlanIdEqualTo(plan.getId());
        planDetailMapper.deleteByExample(planDetailExample);
        List<PlanDetail> detailList = planDetails.getDetailList();
        detailList.forEach(d -> d.setPlanId(plan.getId()));
        planDetailMapper.insertBatch(detailList, loginId);

        PlanMaterialExample planMaterialExample = new PlanMaterialExample();
        PlanMaterialExample.Criteria criteria2 = planMaterialExample.createCriteria();
        criteria2.andPlanIdEqualTo(plan.getId());
        planMaterialMapper.deleteByExample(planMaterialExample);
        List<PlanMaterial> materialList = planDetails.getMaterialList();
        materialList.forEach(m -> m.setPlanId(plan.getId()));
        planMaterialMapper.insertBatch(materialList, loginId);
        return 1;
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
    public List<Plan> findPlanPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
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
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return Optional.ofNullable(planMapper.selectByExample(example)).orElse(Lists.newArrayList());
    }

    @Override
    public List<Plan> findPlanAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize) {
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
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
        }
        return Optional.ofNullable(planMapper.findPlanAndDetailList(example)).orElse(Lists.newArrayList());
    }

    @Override
    public long findPlanRowNum(String productName, String sampleCode, String orderCode, Integer status) {
        PlanExample example = new PlanExample();
        PlanExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeletedEqualTo(DeletedEnum.NO.getKey());
        if (Objects.nonNull(status)) {
            criteria.andStatusEqualTo(status);
        }
        if(StringUtils.isNotEmpty(productName)){
            criteria.andProductNameLike("%" + productName + "%");
        }
        if(StringUtils.isNotEmpty(sampleCode)){
            criteria.andSampleCodeLike("%" + sampleCode + "%");
        }
        if(StringUtils.isNotEmpty(orderCode)){
            criteria.andOrderCodeLike("%" + orderCode + "%");
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
        if(PlanStatus.AUDIT.getKey() != plan.getStatus().intValue()){
            return -1;
        }
        plan.setStatus(PlanStatus.PRODUCE.getKey());
        plan.setUpdateTime(new Date());
        plan.setModifier(loginId);
        if(planMapper.updateByPrimaryKeySelective(plan) <= 0){
            LOGGER.error("修改计划单状态【{}】失败！", plan.getStatus());
            return 0;
        }
        if(orderService.updateStatus(plan.getOrderId(), OrderStatus.PRODUCE.getKey(), OrderStatus.PLAN.getKey(), loginId) <= 0){
            LOGGER.info("修改订单状态失败！");
        }
        return 1;
    }

    @Override
    public int updateToAudit(int id, int loginId) {
        Plan plan = this.getPlanById(id);
        if(Objects.isNull(plan)){
            return 0;
        }
        if(PlanStatus.SAVE.getKey() != plan.getStatus() && PlanStatus.REJECT.getKey() != plan.getStatus()){
            LOGGER.error("生产计划【{}】状态【{}】不正确", id, plan.getStatus());
            return -1;
        }
        plan.setStatus(PlanStatus.AUDIT.getKey());
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

    @Override
    public int updateToReject(int id, int loginId) {
        Plan plan = this.getPlanById(id);
        if(Objects.isNull(plan)){
            return 0;
        }
        if(PlanStatus.AUDIT.getKey() != plan.getStatus()){
            LOGGER.error("生产计划【{}】状态【{}】不正确", id, plan.getStatus());
            return -1;
        }
        plan.setStatus(PlanStatus.REJECT.getKey());
        plan.setUpdateTime(new Date());
        plan.setModifier(loginId);
        return planMapper.updateByPrimaryKeySelective(plan);
    }
}
