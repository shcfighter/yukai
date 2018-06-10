package com.zero.mapper;

import com.zero.model.ProductApplyDetail;
import com.zero.model.example.ProductApplyDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductApplyDetailMapper {
    long countByExample(ProductApplyDetailExample example);

    int deleteByExample(ProductApplyDetailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductApplyDetail record);

    int insertSelective(ProductApplyDetail record);

    List<ProductApplyDetail> selectByExample(ProductApplyDetailExample example);

    ProductApplyDetail selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProductApplyDetail record, @Param("example") ProductApplyDetailExample example);

    int updateByExample(@Param("record") ProductApplyDetail record, @Param("example") ProductApplyDetailExample example);

    int updateByPrimaryKeySelective(ProductApplyDetail record);

    int updateByPrimaryKey(ProductApplyDetail record);

    int insertBatch(@Param("list") List<ProductApplyDetail> list, @Param("loginId") int loginId);
}