package com.baffang.common.entity;

import com.baffang.common.enums.entityEnum.OperationStatusEnum;
import lombok.Data;
import java.util.Date;

/**
 * 系统日志实体
 */
@Data
public class SystemLog {

    private Integer id;

    /**
     * 操作用户ID（用户端操作）
     */
    private Integer userId;

    /**
     * 操作管理员ID（管理端操作）
     */
    private Integer adminId;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作行为
     */
    private String action;

    /**
     * 请求方法：GET/POST/PUT/DELETE
     */
    private String method;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作状态：0-失败，1-成功（使用OperationStatusEnum）
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行时长（毫秒）
     */
    private Integer executionTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
