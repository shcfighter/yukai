package com.zero.mapper;

import com.zero.model.PlanDetail;
import com.zero.model.example.PlanDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PlanDetailMapper {
    long countByExample(PlanDetailExample example);

    int deleteByExample(PlanDetailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PlanDetail record);

    int insertSelective(PlanDetail record);

    List<PlanDetail> selectByExample(PlanDetailExample example);

    PlanDetail selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PlanDetail record, @Param("example") PlanDetailExample example);

    int updateByExample(@Param("record") PlanDetail record, @Param("example") PlanDetailExample example);

    int updateByPrimaryKeySelective(PlanDetail record);

    int updateByPrimaryKey(PlanDetail record);

    int insertBatch(@Param("list") List<PlanDetail> list, @Param("loginId") int loginId);
}