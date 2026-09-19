package com.baffang.common.constant;

public class AuthConstants {
    /**
     * 认证请求头名称
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 用户端登录URL
     */
    public static final String USER_LOGIN_URL = "/api/v1/user/auth/login";

    /**
     * 管理端登录URL
     */
    public static final String ADMIN_LOGIN_URL = "/api/v1/admin/auth/login";

    /**
     * 用户端注册URL
     */
    public static final String USER_REGISTER_URL = "/api/v1/user/auth/register";

    /**
     * 密码重置URL
     */
    public static final String RESET_PASSWORD_URL = "/api/v1/user/auth/reset-password";

    /**
     * 刷新令牌URL
     */
    public static final String REFRESH_TOKEN_URL = "/api/v1/user/auth/refresh-token";

    /**
     * 权限标识前缀
     */
    public static final String PERMISSION_PREFIX = "PERMISSION_";

    /**
     * 用户角色前缀
     */
    public static final String USER_ROLE_PREFIX = "ROLE_USER_";

    /**
     * 管理员角色前缀
     */
    public static final String ADMIN_ROLE_PREFIX = "ROLE_ADMIN_";

    /**
     * 匿名用户角色
     */
    public static final String ANONYMOUS_ROLE = "ROLE_ANONYMOUS";

    /**
     * 登录成功后重定向URL
     */
    public static final String LOGIN_SUCCESS_REDIRECT_URL = "/";

    /**
     * 登录失败后重定向URL
     */
    public static final String LOGIN_FAILURE_REDIRECT_URL = "/login?error";

    /**
     * 管理端注册URL
     */
    public static final String ADMIN_REGISTER_URL = "/api/v1/admin/auth/register";

    /**
     * 管理端生成邀请码URL
     */
    public static final String ADMIN_INVITE_CODE_GENERATE_URL = "/api/v1/admin/auth/invite-code/generate";

    /**
     * 管理端邀请码列表URL
     */
    public static final String ADMIN_INVITE_CODE_LIST_URL = "/api/v1/admin/auth/invite-code/list";

    /**
     * 修改管理员身份URL
     */
    public static final String ADMIN_MEMBER_TYPE_UPDATE_URL = "/api/v1/admin/member/type";

    /**
     * 管理员列表URL
     */
    public static final String ADMIN_MEMBER_LIST_URL = "/api/v1/admin/member/list";

    /**
     * 管理员详情URL
     */
    public static final String ADMIN_MEMBER_DETAIL_URL = "/api/v1/admin/member/{adminId}";

    /**
     * 管理端退出登录URL
     */
    public static final String ADMIN_LOGOUT_URL = "/api/v1/admin/auth/logout";

    /**
     * 平台统计URL
     */
    public static final String ADMIN_STATS_URL = "/api/v1/admin/stats/**";

    /**
     * 用户端获取验证码URL
     */
    public static final String USER_VERIFICATION_CODE_URL = "/api/v1/user/client/getVerificationcode";

    /**
     * 用户端注册URL（实际使用）
     */
    public static final String USER_CLIENT_REGISTER_URL = "/api/v1/user/client/register";

    /**
     * 用户端密码登录URL
     */
    public static final String USER_CLIENT_LOGIN_URL = "/api/v1/user/client/login/password";

    /**
     * 用户端验证码登录URL
     */
    public static final String USER_CLIENT_LOGIN_CODE_URL = "/api/v1/user/client/login/code";

    /**
     * 用户端重置密码URL
     */
    public static final String USER_CLIENT_RESET_PASSWORD_URL = "/api/v1/user/client/ResetPassword";

    /**
     * 私桩认证审核URL
     */
    public static final String ADMIN_STATION_AUTH_URL = "/api/v1/admin/auth/station/**";

    /**
     * 文件下载URL
     */
    public static final String FILE_DOWNLOAD_URL = "/api/v1/user/file/download/**";

    /**
     * 删除邀请码URL
     */
    public static final String ADMIN_INVITE_CODE_DELETE_URL = "/api/v1/admin/invite-code/{codeId}";

    public static final String ADMIN_PRIVATE_STATION_PAGE_URL = "/api/v1/admin/private-station/page";
    public static final String ADMIN_PRIVATE_STATION_DETAIL_URL = "/api/v1/admin/private-station/{id}";
    public static final String ADMIN_PRIVATE_STATION_UPDATE_URL = "/api/v1/admin/private-station/{id}";
    public static final String ADMIN_PRIVATE_STATION_DELETE_URL = "/api/v1/admin/private-station/{id}";
    public static final String ADMIN_PRIVATE_STATION_BATCH_DELETE_URL = "/api/v1/admin/private-station/batch";
    public static final String ADMIN_PRIVATE_STATION_STATS_URL = "/api/v1/admin/private-station/stats";

}