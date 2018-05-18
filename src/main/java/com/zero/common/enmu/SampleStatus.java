package com.zero.common.enmu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SampleStatus {

    NEW(0, "新增"),
    FINISHED(1, "完成"),
    PRODUCE(2, "计划生产");

    int key;
    String value;
}
