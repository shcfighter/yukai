package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OrderOutgoingStatus implements EnumDict {
    SAVE(0, "草稿"),
    FINISHED(1, "完成");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(OrderOutgoingStatus.values()).collect(Collectors.toMap(OrderOutgoingStatus::getKey, OrderOutgoingStatus::getDesc));
    }
}
