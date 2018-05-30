package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum GoodsGoodsType implements EnumDict {
    PURCHASE(1, "采购入库申请"),
    PRODUCT(2, "成品入库申请");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(GoodsGoodsType.values()).collect(Collectors.toMap(GoodsGoodsType::getKey, GoodsGoodsType::getDesc));
    }
}
