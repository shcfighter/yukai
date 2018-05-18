package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    SAVE(0, "录单"),
    SAMPLE(1, "打样"),
    PURCHASE(2, "采购"),
    PLAN(3, "排单 计划"),
    PRODUCE(4, "生产"),
    storage(5, "入库"),
    invalid(-1, "作废");
    int key;
    String desc;
}
