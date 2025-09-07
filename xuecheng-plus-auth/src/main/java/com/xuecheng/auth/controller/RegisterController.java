package com.xuecheng.auth.controller;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.RestResponse;
import com.xuecheng.ucenter.model.dto.UserRegisterDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * @author Mr.M
 * @version 1.0
 * @description 测试controller
 * @date 2022/9/27 17:25
 */
@Slf4j
@RestController
public class RegisterController {

    @Autowired
    UserService userService;

    @Autowired
    XcUserMapper userMapper;



    @PostMapping("/register")
    public void register(@RequestBody @Validated UserRegisterDto userRegisterDto) {
        if(!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmpwd())) {
            XueChengPlusException.cast("两次密码不一致");
        }

        userService.register(userRegisterDto);
    }

    @PostMapping("/findpassword")
    public void findpassword(@RequestBody UserRegisterDto userRegisterDto) {
        if(StringUtils.isBlank(userRegisterDto.getEmail())) {
            XueChengPlusException.cast("邮箱不能为空");
        }

        if(StringUtils.isBlank(userRegisterDto.getPassword()) || StringUtils.isBlank(userRegisterDto.getConfirmpwd())) {
            XueChengPlusException.cast("密码不能为空");
        }

        if(!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmpwd())) {
            XueChengPlusException.cast("两次密码不一致");
        }

        userService.updatePassword(userRegisterDto);
    }

    /**
     * 下面2个接口是测试用的
     * @return
     */
    @GetMapping("/login-success")
    public String loginSuccess() {
        return "登录成功";
    }

    @GetMapping("/r/r1")
    public String r1() {
        return "访问r1资源";
    }
}
