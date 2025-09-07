package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.UserRegisterDto;
import com.xuecheng.ucenter.model.po.XcUser;

public interface UserService {

    public void register(UserRegisterDto userRegisterDto);

    void updatePassword(UserRegisterDto userRegisterDto);
}
