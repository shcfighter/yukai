package com.zero.mapper;

import com.zero.model.OutboundMaterial;
import com.zero.model.example.OutboundMaterialExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OutboundMaterialMapper {
    long countByExample(OutboundMaterialExample example);

    int deleteByExample(OutboundMaterialExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OutboundMaterial record);

    int insertSelective(OutboundMaterial record);

    List<OutboundMaterial> selectByExample(OutboundMaterialExample example);

    OutboundMaterial selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OutboundMaterial record, @Param("example") OutboundMaterialExample example);

    int updateByExample(@Param("record") OutboundMaterial record, @Param("example") OutboundMaterialExample example);

    int updateByPrimaryKeySelective(OutboundMaterial record);

    int updateByPrimaryKey(OutboundMaterial record);

    int insertBatch(@Param("list") List<OutboundMaterial> list, @Param("loginId") int loginId);
}