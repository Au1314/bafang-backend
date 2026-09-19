package com.baffang.config;

import com.baffang.common.constant.AuthConstants;
import com.baffang.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
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
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/files/**",
                                "/doc.html")
                        .permitAll()
                        .anyRequest().authenticated());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(1)
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService, adminDetailsService);
    }
}