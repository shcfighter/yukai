package com.zero.service;

import com.zero.model.Plan;

import java.util.List;

public interface IPlanService {

    int insert(Plan plan, int loginId);

    int update(Plan plan, int loginId);

    int delete(int id, int loginId);

    Plan getPlanById(int id);

    List<Plan> findPlanPage(String productName, String batchNo, Integer status, Integer pageNum, Integer pageSize);

    long findPlanRowNum(String productName, String batchNo, Integer status);

    int updateToProduce(int id, int loginId);

    int updateToFinish(int id, int loginId);
}
