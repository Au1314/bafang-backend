package com.baffang.common.enums;

/**
 * 错误信息枚举
 */

public enum ErrorMsg {
    // 随业务开发随时添加
    // 账号相关错误（1000系列）
    PASSWORD_ERROR(1001, "密码错误"),
    ACCOUNT_NOT_FOUND(1002, "账号不存在"),
    ACCOUNT_LOCKED(1003, "账号被锁定"),
    UNKNOWN_ERROR(1004, "未知错误"),
    USER_NOT_LOGIN(1005, "用户未登录"),
    LOGIN_FAILED(1006, "登录失败"),
    PASSWORD_EDIT_FAILED(1007, "密码修改失败"),

    // 系统通用错误(1500系列)
    ARGUMENT_NOT_VALID(1500, "参数校验错误"),
    SQL_EXCEPTION(1501, "MYSQL出现错误"),
    UPLOAD_FAILED(1502, "文件上传失败"),
    DOWNLOAD_FAILED(1503, "文件下载失败"),
    FILE_NOT_FOUND(1504, "文件不存在"),

    // 认证相关错误码 (2000-2999)
    UNAUTHORIZED(2000, "未授权或token已过期"),
    TOKEN_INVALID(2002, "token无效"),
    FORBIDDEN(2003, "无访问权限"),

    // 参数错误码 (3000-3999)
    PARAM_ERROR(3000, "参数错误"),
    PARAM_MISSING(3001, "参数缺失"),

    // 管理端相关错误（4000系列）
    PASSWORD_NOT_MATCH(4001, "两次输入的密码不一致"),
    INVITE_CODE_INVALID(4002, "邀请码无效"),
    INVITE_CODE_EXPIRED(4003, "邀请码已过期"),
    INVITE_CODE_USED_UP(4004, "邀请码已被使用完毕"),
    USERNAME_EXISTS(4005, "用户名已存在"),
    NOT_SUPER_ADMIN(4006, "只有超级管理员可以执行此操作"),
    CANNOT_CREATE_SUPER_ADMIN(4007, "不允许通过邀请码创建超级管理员"),
    // 管理端相关错误（4000系列）
    CANNOT_MODIFY_SELF(4008, "不能修改自己的身份"),
    CANNOT_MODIFY_SUPER_ADMIN(4009, "不能修改超级管理员的身份"),
    INVALID_TARGET_TYPE(4010, "无效的目标身份类型"),
    NO_PERMISSION_TO_SET_TYPE(4011, "无权设置该身份类型"),
    TARGET_ADMIN_NOT_FOUND(4012, "目标管理员不存在"),
    NO_PERMISSION_TO_MODIFY_TARGET(4012, "无权修改该管理员的身份"),
    NO_PERMISSION(4003, "无访问权限"),
    AUDIT_RECORD_NOT_FOUND(4050, "审核记录不存在"),
    AUDIT_STATUS_INVALID(4051, "当前审核状态不允许此操作"),
    AUDIT_CANNOT_WITHDRAW(4052, "只有待审核状态才能撤回"),
    INVITE_CODE_NOT_FOUND(4060, "邀请码不存在"),
    CANNOT_DELETE_OTHERS_INVITE_CODE(4061, "只能删除自己创建的邀请码"),
    CANNOT_MODIFY_OTHERS_INVITE_CODE(4062, "只能修改自己创建的邀请码"),
    DATA_NOT_FOUND(4063, "数据未找到"),

    // 用户端私桩相关错误（5000系列）
    PRIVATE_STATION_NOT_EXIST(5043, "私桩不存在"),
    INVALID_TIME_FORMAT(5044, "时间格式无效"),
    RESERVE_CREATE_FAILED(5045, "预约创建失败"),
    RESERVE_NOT_FOUND_OR_CANNOT_CANCEL(5046, "预约不存在或无法取消"),
    OWNER_NOT_EXIST(5047, "桩主不存在"),
    DYNAMIC_PRICE_TIME_INVALID(5048, "动态价格时间范围无效"),
    DYNAMIC_PRICE_TIME_OVERLAP(5049, "动态价格时间范围重叠"),

    // 用户端相关错误（5000系列）
    PHONE_NOT_FOUND(5001, "手机号不存在"),
    PHONE_ALREADY_EXISTS(5002, "手机号已存在"),
    REGISTER_FAILED(5003, "注册失败"),
    CAPTCHA_ERROR(5004, "验证码错误"),
    CAR_AUTH_ALREADY_EXISTS(5005, "您已有认证车辆，不可再次提交认证"),
    CAR_AUTH_NOT_FOUND(5006, "无认证车辆记录"),
    ACTIVE_CALL_EXISTS(5007, "您已有正在进行的呼叫，请先处理完当前呼叫"),
    CALL_NOT_EXIST(5008, "呼叫不存在"),
    CALL_STATUS_INVALID(5009, "呼叫状态无效"),
    CANNOT_RESPONSE_OWN_CALL(5010, "不能响应自己的呼叫"),
    STATION_NOT_FOUND(5041, "私桩不存在"),
    STATION_NOT_IN_REVIEW(5042, "该私桩不在审核中状态"),
    REMARK_REQUIRED(5040, "拒绝时备注不能为空"),
    UPDATE_FAILED(5001, "更新失败"),
    NOT_CONVERSATION_PARTICIPANT(5050, "不是对话参与者，无法发送消息"),
    CONVERSATION_NOT_FOUND(5051, "对话不存在"),
    DYNAMIC_PRICE_RULE_NOT_FOUND(5052, "动态价格规则不存在"),

    ;

    // 错误码
    private final int code;
    // 错误信息
    private final String msg;

    ErrorMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // getter方法
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
