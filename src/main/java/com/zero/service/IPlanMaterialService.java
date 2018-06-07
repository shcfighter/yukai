package com.zero.service;

import com.zero.model.PlanMaterial;

import java.util.List;

public interface IPlanMaterialService {

    List<PlanMaterial> getPlanMaterialByOrderId(int planId);
}
