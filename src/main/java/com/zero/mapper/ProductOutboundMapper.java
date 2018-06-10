package com.zero.mapper;

import com.zero.model.ProductOutbound;
import com.zero.model.example.ProductOutboundExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductOutboundMapper {
    long countByExample(ProductOutboundExample example);

    int deleteByExample(ProductOutboundExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductOutbound record);

    int insertSelective(ProductOutbound record);

    List<ProductOutbound> selectByExample(ProductOutboundExample example);

    ProductOutbound selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProductOutbound record, @Param("example") ProductOutboundExample example);

    int updateByExample(@Param("record") ProductOutbound record, @Param("example") ProductOutboundExample example);

    int updateByPrimaryKeySelective(ProductOutbound record);

    int updateByPrimaryKey(ProductOutbound record);

    List<ProductOutbound> findProductOutboundAndDetailList(ProductOutboundExample example);
}