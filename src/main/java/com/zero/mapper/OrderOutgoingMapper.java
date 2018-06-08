package com.zero.mapper;

import com.zero.model.OrderOutgoing;
import com.zero.model.example.OrderOutgoingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderOutgoingMapper {
    long countByExample(OrderOutgoingExample example);

    int deleteByExample(OrderOutgoingExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderOutgoing record);

    int insertSelective(OrderOutgoing record);

    List<OrderOutgoing> selectByExample(OrderOutgoingExample example);

    OrderOutgoing selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OrderOutgoing record, @Param("example") OrderOutgoingExample example);

    int updateByExample(@Param("record") OrderOutgoing record, @Param("example") OrderOutgoingExample example);

    int updateByPrimaryKeySelective(OrderOutgoing record);

    int updateByPrimaryKey(OrderOutgoing record);
}