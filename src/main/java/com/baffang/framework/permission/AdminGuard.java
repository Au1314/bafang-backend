package com.baffang.framework.permission;

import com.baffang.admin.mapper.AdminMapper;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 管理端统一权限守卫（安全整改）
 * 按当前登录用户名实时查库获取管理员身份与角色，不再信任 token claims——
 * 管理员被降权/封禁后旧 token 立即失效；普通用户 token 的用户名不在 admin_info 表中，直接拒绝
 * （同时消除 adminId/userId 两套自增序列撞号导致的越权风险）。
 *
 * maxType 语义与各控制器原 checkAdminPermission 一致：type 越小权限越大，type <= maxType 通过。
 */
@Component
public class AdminGuard {

    /** 管理员身份的最大合法 type（10-未分配身份），requireAdmin(ANY_ADMIN) 表示"任意已注册管理员" */
    public static final int ANY_ADMIN = 10;

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 校验当前请求者是否为管理员且角色满足 maxType，返回管理员 ID
     *
     * @param maxType 允许的最大管理员类型（0-超管，1-普管，2-审核员，3-客服，4-紧急响应，5-论坛管理员，10-未分配）
     */
    public Integer requireAdmin(int maxType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BusinessException(ErrorMsg.UNAUTHORIZED);
        }

        AdminInfo admin = adminMapper.selectByUsername(authentication.getName());
        if (admin == null || Integer.valueOf(1).equals(admin.getIsDeleted())) {
            // 用户名不在管理员表（如普通用户 token 冒充），或已被删除/封禁
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        if (admin.getType() == null || admin.getType() > maxType) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }
        return admin.getId();
    }
}
