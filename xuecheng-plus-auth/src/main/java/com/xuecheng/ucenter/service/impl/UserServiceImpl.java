package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.RandomStringUtil;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.UserRegisterDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    XcUserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;


    @Transactional
    @Override
    public void register(UserRegisterDto userRegisterDto) {
        // 校验邮箱验证码
        Boolean verify = checkCodeClient.verify(userRegisterDto.getCheckcodekey(), userRegisterDto.getCheckcode());
        if (!verify) {
            XueChengPlusException.cast("验证码错误");
        }

        // 查询邮箱是否已注册
        LambdaQueryWrapper<XcUser> wrapper = new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getEmail, userRegisterDto.getEmail());
        List<XcUser> xcUsers = userMapper.selectList(wrapper);
        if (xcUsers.size() > 0) {
            XueChengPlusException.cast("该邮箱已注册");
        }

        XcUser xcUser = new XcUser();
        BeanUtils.copyProperties(userRegisterDto, xcUser);
        xcUser.setId(RandomStringUtil.getRandomString(64));
        xcUser.setName(userRegisterDto.getUsername());
        xcUser.setSex("1");
        xcUser.setUtype("101001");
        xcUser.setStatus("1");
        xcUser.setCreateTime(LocalDateTime.now());
        String encodePassword = passwordEncoder.encode(userRegisterDto.getPassword());
        xcUser.setPassword(encodePassword);
        userMapper.insert(xcUser);
    }

    @Transactional
    @Override
    public void updatePassword(UserRegisterDto userRegisterDto) {
        // 校验邮箱验证码
        Boolean verify = checkCodeClient.verify(userRegisterDto.getCheckcodekey(), userRegisterDto.getCheckcode());
        if (!verify) {
            XueChengPlusException.cast("验证码错误");
        }

        LambdaQueryWrapper<XcUser> wrapper = new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getEmail, userRegisterDto.getEmail());
        List<XcUser> xcUsers = userMapper.selectList(wrapper);
        if (xcUsers.size() != 1) {
            XueChengPlusException.cast("用户不存在或数据异常");
        }

        XcUser xcUser = xcUsers.get(0);
        String encodePassword = passwordEncoder.encode(userRegisterDto.getPassword());
        xcUser.setPassword(encodePassword);
        userMapper.updateById(xcUser);
    }
}
