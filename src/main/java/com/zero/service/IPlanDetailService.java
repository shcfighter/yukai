package com.zero.service;

import com.zero.model.PlanDetail;

import java.util.List;

public interface IPlanDetailService {

    List<PlanDetail> getPlanDetailByOrderId(int planId);
}
