package com.zero.mapper;

import com.zero.model.MaterialOutbound;
import com.zero.model.example.MaterialOutboundExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MaterialOutboundMapper {
    long countByExample(MaterialOutboundExample example);

    int deleteByExample(MaterialOutboundExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MaterialOutbound record);

    int insertSelective(MaterialOutbound record);

    List<MaterialOutbound> selectByExample(MaterialOutboundExample example);

    MaterialOutbound selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MaterialOutbound record, @Param("example") MaterialOutboundExample example);

    int updateByExample(@Param("record") MaterialOutbound record, @Param("example") MaterialOutboundExample example);

    int updateByPrimaryKeySelective(MaterialOutbound record);

    int updateByPrimaryKey(MaterialOutbound record);
}