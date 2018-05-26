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
public enum DeletedEnum implements EnumDict {
    YES(1, "删除"),
    NO(0, "未删除");

    private int key;
    private String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(DeletedEnum.values()).collect(Collectors.toMap(DeletedEnum::getKey, DeletedEnum::getDesc));
    }
}
