package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum PurchaseOrderStatus implements EnumDict {
    SAVE(0, "草稿"),
    PURCHASING(1, "采购中"),
    AUDIT(2, "待入库"),
    FINISHED(3, "已入库"),
    REJECT(4, "驳回");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(PurchaseOrderStatus.values()).collect(Collectors.toMap(PurchaseOrderStatus::getKey, PurchaseOrderStatus::getDesc));
    }
}
