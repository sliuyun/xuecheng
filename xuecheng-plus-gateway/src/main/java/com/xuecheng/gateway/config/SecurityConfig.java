package com.xuecheng.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author Mr.M
 * @version 1.0
 * @description 安全配置类
 * @date 2022/9/27 12:07
 */
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    /**
     * 安全拦截配置
     * 网关过滤器中用到了OAuth2的TokenStore，所以需要配置SpringSecurity过滤器链，放行所有请求
     * @param http
     * @return
     */
    @Bean
    public SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {

        return http.authorizeExchange()
                .pathMatchers("/**").permitAll()    // 放行所有请求
                .and().csrf().disable().build();    // 禁用csrf
    }
}
