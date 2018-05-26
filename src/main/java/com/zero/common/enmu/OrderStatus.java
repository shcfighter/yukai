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
    SAMPLE(1, "打样"),
    PURCHASE(2, "采购"),
    PLAN(3, "排单 计划"),
    PRODUCE(4, "生产"),
    STORAGE(5, "入库"),
    INVALID(-1, "作废");
    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(OrderStatus.values()).collect(Collectors.toMap(OrderStatus::getKey, OrderStatus::getDesc));
    }
}
