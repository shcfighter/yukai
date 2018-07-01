package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductApplyStatus implements EnumDict {
    SAVE(0, "草稿"),
    FINISHED(1, "已出库");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(ProductApplyStatus.values()).collect(Collectors.toMap(ProductApplyStatus::getKey, ProductApplyStatus::getDesc));
    }
}
