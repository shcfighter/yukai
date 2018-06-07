package com.zero.model.verify;

import com.zero.model.Plan;
import com.zero.model.PlanDetail;
import com.zero.model.PlanMaterial;
import lombok.Data;

import java.util.List;

@Data
public class PlanDetails {
    private Plan plan;
    private List<PlanDetail> detailList;
    private List<PlanMaterial> materialList;
}
