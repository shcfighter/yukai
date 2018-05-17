package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ValidEnum {
    YES(0, "正常"),
    NO(1, "禁用");
    private int key;
    private String desc;
}
