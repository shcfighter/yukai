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
public enum ValidEnum implements EnumDict {
    YES(0, "正常"),
    NO(1, "禁用");

    private int key;
    private String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(ValidEnum.values()).collect(Collectors.toMap(ValidEnum::getKey, ValidEnum::getDesc));
    }
}
