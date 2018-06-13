package com.zero.mapper;

import com.zero.model.OrderBatch;
import com.zero.model.example.OrderBatchExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderBatchMapper {
    long countByExample(OrderBatchExample example);

    int deleteByExample(OrderBatchExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderBatch record);

    int insertSelective(OrderBatch record);

    List<OrderBatch> selectByExample(OrderBatchExample example);

    OrderBatch selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OrderBatch record, @Param("example") OrderBatchExample example);

    int updateByExample(@Param("record") OrderBatch record, @Param("example") OrderBatchExample example);

    int updateByPrimaryKeySelective(OrderBatch record);

    int updateByPrimaryKey(OrderBatch record);

    int insertBatch(@Param("list") List<OrderBatch> list, @Param("loginId") int loginId);

    List<OrderBatch> findOrderBatchAndOrderDetailList(OrderBatchExample example);
}