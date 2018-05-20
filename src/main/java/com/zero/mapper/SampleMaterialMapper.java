package com.zero.mapper;

import com.zero.model.SampleMaterial;

import java.util.List;

import com.zero.model.example.SampleMaterialExample;
import org.apache.ibatis.annotations.Param;

public interface SampleMaterialMapper {
    long countByExample(SampleMaterialExample example);

    int deleteByExample(SampleMaterialExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SampleMaterial record);

    int insertSelective(SampleMaterial record);

    List<SampleMaterial> selectByExample(SampleMaterialExample example);

    SampleMaterial selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SampleMaterial record, @Param("example") SampleMaterialExample example);

    int updateByExample(@Param("record") SampleMaterial record, @Param("example") SampleMaterialExample example);

    int updateByPrimaryKeySelective(SampleMaterial record);

    int updateByPrimaryKey(SampleMaterial record);
}