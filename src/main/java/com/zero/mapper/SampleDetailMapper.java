package com.zero.mapper;

import com.zero.model.SampleDetail;
import com.zero.model.example.SampleDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SampleDetailMapper {
    long countByExample(SampleDetailExample example);

    int deleteByExample(SampleDetailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SampleDetail record);

    int insertSelective(SampleDetail record);

    List<SampleDetail> selectByExample(SampleDetailExample example);

    SampleDetail selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SampleDetail record, @Param("example") SampleDetailExample example);

    int updateByExample(@Param("record") SampleDetail record, @Param("example") SampleDetailExample example);

    int updateByPrimaryKeySelective(SampleDetail record);

    int updateByPrimaryKey(SampleDetail record);

    int insertBatch(@Param("list") List<SampleDetail> list, @Param("loginId") int loginId);
}