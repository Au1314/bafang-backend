package com.baffang.common.constant;

public class JwtConstants {
    /**
     * JWT签名密钥（安全整改：不再硬编码——由 config/JwtKeyConfig 在启动时从配置注入，
     * 来源为环境变量 JWT_SECRET 或配置项 jwt.secret；未配置时应用启动失败）
     */
    public static volatile String SECRET_KEY;

    /**
     * JWT令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌过期时间（毫秒） - 用户端默认7天
     */
    public static final long USER_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    /**
     * 令牌过期时间（毫秒） - 管理端默认1天
     */
    public static final long ADMIN_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000;

    /**
     * 刷新令牌过期时间（毫秒） - 默认30天
     */
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000;

    /**
     * 令牌中用户ID的键
     */
    public static final String USER_ID_CLAIM = "userId";

    /**
     * 令牌中用户名的键
     */
    public static final String USERNAME_CLAIM = "username";

    /**
     * 令牌中角色的键
     */
    public static final String ROLE_CLAIM = "role";

    /**
     * 令牌中认证类型的键
     */
    public static final String AUTH_TYPE_CLAIM = "authType";

    /**
     * 令牌中权限的键
     */
    public static final String PERMISSIONS_CLAIM = "permissions";
}