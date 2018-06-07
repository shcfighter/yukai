package com.zero.mapper;

import com.zero.model.PlanMaterial;
import com.zero.model.example.PlanMaterialExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PlanMaterialMapper {
    long countByExample(PlanMaterialExample example);

    int deleteByExample(PlanMaterialExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PlanMaterial record);

    int insertSelective(PlanMaterial record);

    List<PlanMaterial> selectByExample(PlanMaterialExample example);

    PlanMaterial selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PlanMaterial record, @Param("example") PlanMaterialExample example);

    int updateByExample(@Param("record") PlanMaterial record, @Param("example") PlanMaterialExample example);

    int updateByPrimaryKeySelective(PlanMaterial record);

    int updateByPrimaryKey(PlanMaterial record);

    int insertBatch(@Param("list") List<PlanMaterial> list, @Param("loginId") int loginId);
}