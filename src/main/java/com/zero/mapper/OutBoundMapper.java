package com.zero.mapper;

import com.zero.model.OutBound;
import com.zero.model.example.OutBoundExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OutBoundMapper {
    long countByExample(OutBoundExample example);

    int deleteByExample(OutBoundExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OutBound record);

    int insertSelective(OutBound record);

    List<OutBound> selectByExample(OutBoundExample example);

    OutBound selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OutBound record, @Param("example") OutBoundExample example);

    int updateByExample(@Param("record") OutBound record, @Param("example") OutBoundExample example);

    int updateByPrimaryKeySelective(OutBound record);

    int updateByPrimaryKey(OutBound record);
}