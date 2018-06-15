package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum MessageType implements EnumDict {
    SYSTEM(0, "系统消息"),
    BUSINESS(1, "业务消息");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(MessageType.values()).collect(Collectors.toMap(MessageType::getKey, MessageType::getDesc));
    }
}
