package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum  PlanStatus implements EnumDict {
    SAVE(0, "新建"),
    AUDIT(1, "审核"),
    PRODUCE(2, "生产"),
    FINISHED(3, "完成"),
    REJECT(4, "驳回");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(PlanStatus.values()).collect(Collectors.toMap(PlanStatus::getKey, PlanStatus::getDesc));
    }
}
