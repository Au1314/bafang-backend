package com.baffang.config;

import com.baffang.common.constant.AuthConstants;
import com.baffang.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final UserDetailsService adminDetailsService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(
            @Qualifier("userDetailsService") UserDetailsService userDetailsService,
            @Qualifier("adminDetailsService") UserDetailsService adminDetailsService,
            JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.adminDetailsService = adminDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        // 1. 创建空缓存实例（Spring内置，不会空指针）
        UserCache nullUserCache = new NullUserCache();

        // 2. 用户认证提供者：替换setUserCache(null)为setUserCache(nullUserCache)
        DaoAuthenticationProvider userProvider = new DaoAuthenticationProvider(userDetailsService);
        userProvider.setPasswordEncoder(passwordEncoder());
        userProvider.setUserCache(nullUserCache); // 关键修复：使用空缓存实现，而非null

        // 3. 管理员认证提供者：同理
        DaoAuthenticationProvider adminProvider = new DaoAuthenticationProvider(adminDetailsService);
        adminProvider.setPasswordEncoder(passwordEncoder());
        adminProvider.setUserCache(nullUserCache); // 关键修复

        // 4. 构建ProviderManager
        ProviderManager providerManager = new ProviderManager(userProvider, adminProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 登录/注册/公开资源等无需认证的接口 */
    private static final String[] PUBLIC_ENDPOINTS = {
            AuthConstants.USER_LOGIN_URL,
            AuthConstants.ADMIN_LOGIN_URL,
            AuthConstants.USER_REGISTER_URL,
            AuthConstants.USER_CLIENT_REGISTER_URL,
            AuthConstants.USER_CLIENT_LOGIN_URL,
            AuthConstants.USER_CLIENT_LOGIN_CODE_URL,
            AuthConstants.RESET_PASSWORD_URL,
            AuthConstants.USER_CLIENT_RESET_PASSWORD_URL,
            AuthConstants.REFRESH_TOKEN_URL,
            AuthConstants.ADMIN_REGISTER_URL,
            AuthConstants.ADMIN_LOGOUT_URL,
            AuthConstants.USER_VERIFICATION_CODE_URL,
            AuthConstants.FILE_DOWNLOAD_URL,
            "/ws/**",
            "/files/**"
    };

    /** 接口文档端点：仅开发环境暴露（安全整改：生产环境不再公开 doc.html / swagger） */
    private static final String[] DOC_ENDPOINTS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/doc.html"
    };

    private SecurityFilterChain buildChain(HttpSecurity http, boolean exposeDocs) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
                    if (exposeDocs) {
                        authorize.requestMatchers(DOC_ENDPOINTS).permitAll();
                    }
                    authorize.anyRequest().authenticated();
                });

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 生产环境（prod profile）：不暴露接口文档 */
    @Bean
    @Order(1)
    @Profile("prod")
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        return buildChain(http, false);
    }

    /** 开发/默认环境：保留 Knife4j 与 Swagger 文档 */
    @Bean
    @Order(2)
    @Profile("!prod")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return buildChain(http, true);
    }

    @Bean
    @Order(1)
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService, adminDetailsService);
    }
}