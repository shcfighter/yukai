package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SampleStatus implements EnumDict {

    NEW(0, "新增"),
    FINISHED(1, "完成"),
    PRODUCE(2, "计划生产");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(SampleStatus.values()).collect(Collectors.toMap(SampleStatus::getKey, SampleStatus::getDesc));
    }
}
