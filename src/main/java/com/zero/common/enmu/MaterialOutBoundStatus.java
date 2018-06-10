package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum MaterialOutBoundStatus implements EnumDict {
    SAVE(0, "草稿"),
    AUDIT(1, "申请出库"),
    FINISHED(2, "出库完成"),
    REJECT(3, "驳回");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(MaterialOutBoundStatus.values()).collect(Collectors.toMap(MaterialOutBoundStatus::getKey, MaterialOutBoundStatus::getDesc));
    }
}
