package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum GoodsType implements EnumDict {
    SAVE(1, "原材料"),
    UPDATE(2, "成品");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(GoodsType.values()).collect(Collectors.toMap(GoodsType::getKey, GoodsType::getDesc));
    }
}
