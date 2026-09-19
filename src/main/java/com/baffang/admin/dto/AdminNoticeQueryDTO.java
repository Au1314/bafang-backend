package com.baffang.admin.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理端通知查询请求 DTO
 */
@Data
public class AdminNoticeQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通知类型（可选） */
    private Integer type;

    /** 接收用户ID（可选） */
    private Integer userId;

    /** 是否已读：0-未读，1-已读（可选） */
    private Integer isRead;

    /** 关键词搜索（可选） */
    private String keyword;

    /** 页码 */
    private Integer page = 1;

    /** 每页条数 */
    private Integer pageSize = 20;

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

    public Integer getPage() {
        // 页码小于1时，默认返回1
        return (page == null || page < 1) ? 1 : page;
    }

    public Integer getSize() {
        // 每页条数小于1或大于100时，默认返回20
        return (pageSize == null || pageSize < 1 || pageSize > 100) ? 20 : pageSize;
    }
}
