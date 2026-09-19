package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SystemLogDetailVO {
    
    private Long id;
    
    private Integer adminId;
    
    private String adminName;
    
    private Integer userId;
    
    private String userName;
    
    private String module;
    
    private String action;
    
    private String method;
    
    private String url;
    
    /**
     * 请求参数
     */
    private String params;
    
    /**
     * 返回结果
     */
    private String result;
    
    private String ip;
    
    private String userAgent;
    
    private Integer status;
    
    /**
     * 错误信息
     */
    private String errorMsg;
    
    private Integer executionTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
