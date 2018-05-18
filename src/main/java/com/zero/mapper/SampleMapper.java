package com.zero.mapper;

import com.zero.model.Sample;
import com.zero.model.example.SampleExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SampleMapper {
    long countByExample(SampleExample example);

    int deleteByExample(SampleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Sample record);

    int insertSelective(Sample record);

    List<Sample> selectByExample(SampleExample example);

    Sample selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Sample record, @Param("example") SampleExample example);

    int updateByExample(@Param("record") Sample record, @Param("example") SampleExample example);

    int updateByPrimaryKeySelective(Sample record);

    int updateByPrimaryKey(Sample record);

    long findSampleRowNum(Map<String, Object> condition);

    List<Map<String, Object>> findSamplePage(Map<String, Object> condition);

    int updateStatus(Map<String, Object> condition);
}