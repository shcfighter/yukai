package com.zero.service;

import com.zero.model.Plan;

import java.util.List;

public interface IPlanService {

    int insert(Plan plan, int loginId);

    int update(Plan plan, int loginId);

    int delete(int id, int loginId);

    Plan getPlanById(int id);

    List<Plan> findPlan(int orderId, String batchNo);

    int updateToProduce(int orderId, int status, int loginId);

    int updateToFinish(int id, int loginId);
}
