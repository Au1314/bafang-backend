package com.baffang.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 平台参数配置（单行配置，id 固定为 1，config_json 存 JSON 对象）
 */
@Data
public class PlatformConfig {

    private Integer id;

    /**
     * 配置内容（JSON 字符串，由服务层解析/序列化）
     */
    private String configJson;

    /**
     * 最后修改管理员ID
     */
    private Integer updateAdminId;

    private Date updateTime;
}
