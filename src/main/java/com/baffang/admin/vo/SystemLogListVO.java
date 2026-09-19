package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SystemLogListVO {
    
    private Long id;
    
    /**
     * 管理员ID
     */
    private Integer adminId;
    
    /**
     * 管理员名称
     */
    private String adminName;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 模块
     */
    private String module;
    
    /**
     * 操作
     */
    private String action;
    
    /**
     * 请求方法
     */
    private String method;
    
    /**
     * 请求URL
     */
    private String url;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 状态：1成功，0失败
     */
    private Integer status;
    
    /**
     * 执行时间(ms)
     */
    private Integer executionTime;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
