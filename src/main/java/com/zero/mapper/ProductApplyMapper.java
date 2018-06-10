package com.zero.mapper;

import com.zero.model.ProductApply;
import com.zero.model.example.ProductApplyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductApplyMapper {
    long countByExample(ProductApplyExample example);

    int deleteByExample(ProductApplyExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductApply record);

    int insertSelective(ProductApply record);

    List<ProductApply> selectByExample(ProductApplyExample example);

    ProductApply selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProductApply record, @Param("example") ProductApplyExample example);

    int updateByExample(@Param("record") ProductApply record, @Param("example") ProductApplyExample example);

    int updateByPrimaryKeySelective(ProductApply record);

    int updateByPrimaryKey(ProductApply record);

    List<ProductApply> findProductAndDetailList(ProductApplyExample example);
}