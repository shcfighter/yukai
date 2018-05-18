package com.zero.mapper;

import com.zero.model.GoodsUseRecord;
import com.zero.model.example.GoodsUseRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoodsUseRecordMapper {
    long countByExample(GoodsUseRecordExample example);

    int deleteByExample(GoodsUseRecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(GoodsUseRecord record);

    int insertSelective(GoodsUseRecord record);

    List<GoodsUseRecord> selectByExample(GoodsUseRecordExample example);

    GoodsUseRecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") GoodsUseRecord record, @Param("example") GoodsUseRecordExample example);

    int updateByExample(@Param("record") GoodsUseRecord record, @Param("example") GoodsUseRecordExample example);

    int updateByPrimaryKeySelective(GoodsUseRecord record);

    int updateByPrimaryKey(GoodsUseRecord record);
}