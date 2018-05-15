package com.zero.model.verify;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class LoginUser {

    private String loginName;
    private String pwd;
}
