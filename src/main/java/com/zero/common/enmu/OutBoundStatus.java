package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OutBoundStatus implements EnumDict {
    SAVE(0, "新建"),
    AUDIT(1, "审核中"),
    FINISHED(2, "已入库"),
    REJECT(3, "驳回");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(OutBoundStatus.values()).collect(Collectors.toMap(OutBoundStatus::getKey, OutBoundStatus::getDesc));
    }
}
