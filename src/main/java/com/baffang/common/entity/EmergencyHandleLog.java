package com.baffang.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 紧急呼叫处理日志
 */
@Data
public class EmergencyHandleLog {

    private Integer id;

    /**
     * 关联紧急呼叫ID
     */
    private Integer callId;

    /**
     * 操作管理员ID
     */
    private Integer adminId;

    /**
     * 日志内容
     */
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
