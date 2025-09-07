package com.xuecheng.checkcode.service.impl;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.AbstractCheckCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 图片验证码生成器
 * @date 2022/9/29 16:16
 */
@Service("EmailCheckCodeService")
public class EmailCheckCodeServiceImpl extends AbstractCheckCodeService {

    @Autowired
    private JavaMailSender mailSender;

    @Resource(name="NumberLetterCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator = checkCodeGenerator;
    }

    @Resource(name="UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }


    @Resource(name="RedisCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore = checkCodeStore;
    }

    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generateResult = generate(checkCodeParamsDto, 6, "emailkey:", 300000);
        String key = generateResult.getKey();
        String code = generateResult.getCode();
        sendEmail(code);
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        checkCodeResultDto.setKey(key);
        return checkCodeResultDto;
    }

    private void sendEmail(String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1349371467@qq.com");
        message.setTo("1349371467@qq.com");

        // 标题
        message.setSubject("注册账号验证码");

        // 正文
        message.setText(content);
        mailSender.send(message);
    }
}
