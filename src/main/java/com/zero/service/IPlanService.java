package com.zero.service;

import com.zero.model.Plan;
import com.zero.model.verify.PlanDetails;

import java.util.List;

public interface IPlanService {

    int insert(PlanDetails planDetails, int loginId);

    int update(PlanDetails planDetails, int loginId);

    int delete(int id, int loginId);

    Plan getPlanById(int id);

    List<Plan> findPlanPage(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    List<Plan> findPlanAndDetailList(String productName, String sampleCode, String orderCode, Integer status, Integer pageNum, Integer pageSize);

    long findPlanRowNum(String productName, String sampleCode, String orderCode, Integer status);

    int updateToProduce(int id, int loginId);

    int updateToAudit(int id, int loginId);

    int updateToFinish(int id, int loginId);

    int updateToReject(int id, int loginId);
}
