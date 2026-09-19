package com.baffang.framework.permission.interceptor;

import com.baffang.admin.mapper.AdminMapper;
import com.baffang.common.constant.PermissionConst;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.entity.UserInfo;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.framework.permission.annotation.RequirePermission;
import com.baffang.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 权限控制拦截器
 * 用于拦截带有@RequirePermission注解的方法，并进行权限验证
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 判断是否是HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查方法上是否有@RequirePermission注解
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            // 检查类上是否有@RequirePermission注解
            requirePermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
            if (requirePermission == null) {
                return true;
            }
        }

        // 检查是否需要登录
        if (requirePermission.needLogin()) {
            // 从SecurityContext中获取当前认证的用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                throw new BusinessException(ErrorMsg.UNAUTHORIZED);
            }
        }

        // 检查是否需要权限验证
        if (requirePermission.needPermission()) {
            String permissionCode = requirePermission.value();
            if (!hasPermission(permissionCode)) {
                throw new BusinessException(ErrorMsg.FORBIDDEN);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 方法执行后调用
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 请求完成后调用
    }

    /**
     * 检查用户是否有指定权限
     * 
     * @param permissionCode 权限编码
     * @return 是否有权限
     */
    private boolean hasPermission(String permissionCode) {
        // 从SecurityContext中获取当前认证的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 获取用户名
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }

        if (username == null) {
            return false;
        }

        // 确定所属系统和角色
        String authType = null;
        Integer role = null;

        // 根据请求路径判断所属系统
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String path = request.getRequestURI();

        if (path.startsWith("/api/v1/admin/")) {
            // 管理员系统
            authType = "admin";
            // 从数据库中查询管理员信息
            AdminInfo adminInfo = adminMapper.selectByUsername(username);
            if (adminInfo != null) {
                role = adminInfo.getType();
            }
        } else if (path.startsWith("/api/v1/user/")) {
            // 用户系统
            authType = "user";
            // 从数据库中查询用户信息
            UserInfo userInfo = userMapper.selectByUsername(username);
            if (userInfo != null) {
                // 根据认证状态确定用户角色
                com.baffang.common.enums.UserRoleEnum userRole = com.baffang.common.enums.UserRoleEnum.fromAuthStatus(
                        userInfo.getAuthVehicle(),
                        userInfo.getAuthChargingStation());
                role = userRole.getCode();
            }
        } else {
            return false;
        }

        if (role == null) {
            return false;
        }

        // 根据所属系统和角色进行权限验证
        return checkPermissionByAuthTypeAndRole(authType, role, permissionCode);
    }

    /**
     * 根据所属系统和角色检查权限
     * 
     * @param authType       所属系统
     * @param role           角色
     * @param permissionCode 权限编码
     * @return 是否有权限
     */
    boolean checkPermissionByAuthTypeAndRole(String authType, Integer role, String permissionCode) {
        // 管理员系统权限验证
        if ("admin".equals(authType)) {
            // 超级管理员有所有权限
            if (0 == role) {
                return true;
            }
            // 普通管理员权限
            if (1 == role && permissionCode.equals(PermissionConst.ADMIN_COMMON_ADMIN)) {
                return true;
            }
            // 审核员权限
            if (2 == role && permissionCode.equals(PermissionConst.ADMIN_AUDITOR)) {
                return true;
            }
            // 客户服务权限
            if (3 == role && permissionCode.equals(PermissionConst.ADMIN_CUSTOMER_SERVICE)) {
                return true;
            }
            // 紧急响应人员权限
            if (4 == role && permissionCode.equals(PermissionConst.ADMIN_EMERGENCY_RESPONDER)) {
                return true;
            }
            // 论坛管理员权限
            if (5 == role && permissionCode.equals(PermissionConst.ADMIN_FORUM_ADMIN)) {
                return true;
            }
        }

        // 用户系统权限验证
        if ("user".equals(authType)) {
            // 普通用户权限
            if (role == 0 && permissionCode.equals(PermissionConst.USER_COMMON_USER)) {
                return true;
            }
            // VIP用户权限
            if (role == 1 && (permissionCode.equals(PermissionConst.USER_COMMON_USER)
                    || permissionCode.equals(PermissionConst.USER_VIP))) {
                return true;
            }
            // 车辆所有者权限
            if (role == 2 && (permissionCode.equals(PermissionConst.USER_COMMON_USER)
                    || permissionCode.equals(PermissionConst.USER_CAR_OWNER))) {
                return true;
            }
            // 充电站所有者权限
            if (role == 3 && (permissionCode.equals(PermissionConst.USER_COMMON_USER)
                    || permissionCode.equals(PermissionConst.USER_STATION_OWNER))) {
                return true;
            }
            // 双重所有者权限（车辆和充电站）
            if (role == 4 && (permissionCode.equals(PermissionConst.USER_COMMON_USER) ||
                    permissionCode.equals(PermissionConst.USER_CAR_OWNER) ||
                    permissionCode.equals(PermissionConst.USER_STATION_OWNER) ||
                    permissionCode.equals(PermissionConst.USER_ALL_OWNER))) {
                return true;
            }
        }

        return false;
    }

}
