package com.xuecheng.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
public class TokenConfig {
    // 签名密钥，使用对称加密
    private static final String SIGNING_KEY = "mq123";

    /**
     * 使用 JWT 存储 Token
     */
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter converter) {
        return new JwtTokenStore(converter);
    }

    /**
     * JWT 令牌转换器（设置签名 key）
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }

    /**
     * JWT 令牌管理服务
     * @param tokenStore JwtTokenStore
     * @param converter  JwtAccessTokenConverter
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices authorizationServerTokenServices(TokenStore tokenStore, JwtAccessTokenConverter converter) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore);

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(converter));
        tokenServices.setTokenEnhancer(tokenEnhancerChain);

        tokenServices.setAccessTokenValiditySeconds(3600 * 12);     // 令牌默认有效期12小时
        tokenServices.setRefreshTokenValiditySeconds(3600 * 24 * 7);    // 刷新令牌默认有效期7天
        return tokenServices;
    }
}
