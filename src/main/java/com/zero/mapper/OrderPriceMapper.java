package com.zero.mapper;

import com.zero.model.OrderPrice;
import com.zero.model.example.OrderPriceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderPriceMapper {
    long countByExample(OrderPriceExample example);

    int deleteByExample(OrderPriceExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderPrice record);

    int insertSelective(OrderPrice record);

    List<OrderPrice> selectByExample(OrderPriceExample example);

    OrderPrice selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OrderPrice record, @Param("example") OrderPriceExample example);

    int updateByExample(@Param("record") OrderPrice record, @Param("example") OrderPriceExample example);

    int updateByPrimaryKeySelective(OrderPrice record);

    int updateByPrimaryKey(OrderPrice record);

    int insertBatch(@Param("list") List<OrderPrice> list, @Param("orderId") int orderId, @Param("loginId") int loginId);
}