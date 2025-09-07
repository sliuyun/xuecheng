package com.xuecheng.ucenter.model.dto;

import com.xuecheng.ucenter.model.po.XcUser;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 学生注册提交信息
 */
@Data
public class UserRegisterDto{
    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String cellphone;

    @NotNull
    private String nickname;

    @NotNull
    private String password;

    @NotNull
    private String confirmpwd;

    @NotNull
    private String checkcodekey;

    @NotNull
    private String checkcode;
}
