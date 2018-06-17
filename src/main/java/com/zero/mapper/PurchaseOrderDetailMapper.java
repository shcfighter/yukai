package com.zero.mapper;

import com.zero.model.PurchaseOrderDetail;
import com.zero.model.example.PurchaseOrderDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PurchaseOrderDetailMapper {
    long countByExample(PurchaseOrderDetailExample example);

    int deleteByExample(PurchaseOrderDetailExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PurchaseOrderDetail record);

    int insertSelective(PurchaseOrderDetail record);

    List<PurchaseOrderDetail> selectByExample(PurchaseOrderDetailExample example);

    PurchaseOrderDetail selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PurchaseOrderDetail record, @Param("example") PurchaseOrderDetailExample example);

    int updateByExample(@Param("record") PurchaseOrderDetail record, @Param("example") PurchaseOrderDetailExample example);

    int updateByPrimaryKeySelective(PurchaseOrderDetail record);

    int updateByPrimaryKey(PurchaseOrderDetail record);

    int insertBatch(@Param("list") List<PurchaseOrderDetail> list, @Param("loginId") int loginId);
}