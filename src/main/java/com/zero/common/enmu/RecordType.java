package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum RecordType implements EnumDict {
    SAVE(1, "新增"),
    UPDATE(2, "修改"),
    INBOUND(3, "入库"),
    OUTBOUND(4, "出库"),
    DELETED(5, "删除");

    int key;
    String desc;

    @Override
    public Map<Integer, String> getDict() {
        return Stream.of(RecordType.values()).collect(Collectors.toMap(RecordType::getKey, RecordType::getDesc));
    }
}
