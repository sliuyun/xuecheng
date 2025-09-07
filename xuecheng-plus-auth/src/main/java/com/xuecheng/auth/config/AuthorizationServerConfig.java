package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * @author Mr.M
 * @version 1.0
 * @description 授权服务器配置
 * @date 2025/8/31 16:25
 */
@Order(2)
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthorizationServerTokenServices authorizationServerTokenServices;

    /**
     * checkTokenAccess("permitAll()") 允许在不登录的情况下通过POST请求校验 token
     * allowFormAuthenticationForClients() 允许在不登录的情况下通过POST请求获取token
     * @param security a fluent configurer for security features
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security

                .checkTokenAccess("permitAll()")                  //oauth/check_token公开
                .allowFormAuthenticationForClients();				//表单认证（申请令牌）
    }

    /**
     * 配置客户端详情
     * 主要用于
     * 1.获取授权码
     * 在浏览器输入：http://localhost:63070/auth/oauth/authorize?response_type=code&client_id=XcWebApp&redirect_uri=http://www.51xuecheng.cn&scope=all
     * 输入账号名和密码，点击授权，即可在弹出的新页面的地址栏获取授权码
     *
     * 2.获取令牌
     * 在Apifox中输入：http://localhost:63070/auth/oauth/token?client_id=XcWebApp&client_secret=XcWebApp&grant_type=authorization_code&code=授权码&redirect_uri=http://www.51xuecheng.cn
     * 结果如下
     * @param clients the client details configurer
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("XcWebApp")
                .secret(passwordEncoder.encode("XcWebApp"))
                .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                .resourceIds("xuecheng-plus")
                .redirectUris("http://www.51xuecheng.cn")
                .scopes("all");
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * tokenService() 添加自定义的Token管理器 用于授权码模式获取令牌
     * authenticationManager() 添加用户认证管理器 用于通过密码模式获取令牌
     *  AuthenticationManager需要再WebSecurityConfig中返回
     * @param endpoints the endpoints configurer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenServices(authorizationServerTokenServices)
                .authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }
}
