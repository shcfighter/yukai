package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum MessageRead implements EnumDict {
    NOT_READ(0, "未读"),
    READ(1, "已读");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(MessageRead.values()).collect(Collectors.toMap(MessageRead::getKey, MessageRead::getDesc));
    }
}
