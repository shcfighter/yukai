package com.zero.mapper;

import com.zero.model.Plan;
import com.zero.model.example.PlanExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PlanMapper {
    long countByExample(PlanExample example);

    int deleteByExample(PlanExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Plan record);

    int insertSelective(Plan record);

    List<Plan> selectByExample(PlanExample example);

    Plan selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Plan record, @Param("example") PlanExample example);

    int updateByExample(@Param("record") Plan record, @Param("example") PlanExample example);

    int updateByPrimaryKeySelective(Plan record);

    int updateByPrimaryKey(Plan record);

    List<Plan> findPlanAndDetailList(PlanExample example);
}