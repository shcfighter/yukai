package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OrderStatus implements EnumDict{
    SAVE(0, "录单"),
    PLAN(1, "计划排期中"),
    PRODUCE(2, "生产中"),
    STORAGE(5, "入库"),
    INVALID(-1, "作废");
    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(OrderStatus.values()).collect(Collectors.toMap(OrderStatus::getKey, OrderStatus::getDesc));
    }
}
