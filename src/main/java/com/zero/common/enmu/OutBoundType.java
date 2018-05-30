package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OutBoundType implements EnumDict {
    PURCHASE(1, "原材料申请出库"),
    PRODUCT(2, "成品申请出库");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(OutBoundType.values()).collect(Collectors.toMap(OutBoundType::getKey, OutBoundType::getDesc));
    }
}
