package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecordType {
    SAVE(1, "新增"),
    UPDATE(2, "修改"),
    INBOUND(3, "入库"),
    OUTBOUND(4, "出库"),
    DELETED(5, "删除");

    int key;
    String desc;
}
