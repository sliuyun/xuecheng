package com.xuecheng.auth.config;

import com.xuecheng.ucenter.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Mr.M
 * @version 1.0
 * @description 安全管理配置
 * @date 2022/9/26 20:53
 */
// @Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 创建下面3个空过滤器
     * 这里有一个很坑的点，匹配路径不包含 server.servlet.context-path
     * 比如，有一个请求，完整路径是 /auth/register，其中/auth是属于server.servlet.context-path，那么要让这个请求放行，规则中应该直接写 /register
     * @param web
     * @throws Exception
     */
    //

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/register", "/r/r1", "/login-success", "/findpassword");
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    DaoAuthenticationProviderCustom daoAuthenticationProviderCustom;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProviderCustom);
    }

    /**
     * 配置了一个可以登录的用户，用户信息存储在内存中
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // manager.createUser(User.withUsername("user")
        //         .password(passwordEncoder.encode("1"))
        //         .roles("USER")
        //         .build());
        UserDetailsServiceImpl manager = new UserDetailsServiceImpl();
        return manager;
    }
}
