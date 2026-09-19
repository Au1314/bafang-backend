package com.baffang.user.aiTools;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.service.UserService;
import com.baffang.user.vo.ProfileVO;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserInfoTools {

    private final UserService userService;

    public UserInfoTools(UserService userService) {
        this.userService = userService;
    }

    @Tool(description = "获取当前登录用户的个人信息")
    public ProfileVO getCurrentUserInfo() {
        // 从当前请求中获取HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        // 从请求头中获取Authorization令牌
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            throw new BusinessException("用户未登录或令牌无效");
        }

        // 去掉Bearer前缀获取token
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "").trim();

        // 解析token获取用户ID
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        if (userId == null) {
            throw new BusinessException("令牌解析失败，无法获取用户ID");
        }

        // 调用UserService获取用户个人信息
        return userService.getProfile(userId);
    }
}
