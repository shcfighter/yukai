package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DeletedEnum {
    YES(1, "删除"),
    NO(0, "未删除");
    private int key;
    private String desc;
}
