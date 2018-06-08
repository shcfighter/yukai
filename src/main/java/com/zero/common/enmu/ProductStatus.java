package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductStatus implements EnumDict {
    SAVE(0, "草稿"),
    AUDIT(1, "待入库"),
    FINISHED(2, "已入库；");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(ProductStatus.values()).collect(Collectors.toMap(ProductStatus::getKey, ProductStatus::getDesc));
    }
}
