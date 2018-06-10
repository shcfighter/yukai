package com.zero.mapper;

import com.zero.model.ProductOutboundDetail;
import com.zero.model.example.ProductOutboundDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductOutboundDetailMapper {
    long countByExample(ProductOutboundDetailExample example);

    int deleteByExample(ProductOutboundDetailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductOutboundDetail record);

    int insertSelective(ProductOutboundDetail record);

    List<ProductOutboundDetail> selectByExample(ProductOutboundDetailExample example);

    ProductOutboundDetail selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProductOutboundDetail record, @Param("example") ProductOutboundDetailExample example);

    int updateByExample(@Param("record") ProductOutboundDetail record, @Param("example") ProductOutboundDetailExample example);

    int updateByPrimaryKeySelective(ProductOutboundDetail record);

    int updateByPrimaryKey(ProductOutboundDetail record);

    int insertBatch(@Param("list") List<ProductOutboundDetail> list, @Param("loginId") int loginId);
}