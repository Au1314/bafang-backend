package com.baffang.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理端通知返回 DTO
 */
@Data
public class AdminNoticeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 接收用户ID，NULL表示全体用户 */
    private Integer userId;

    /** 接收用户名称（关联查询） */
    private String userName;

    private String title;

    private String content;

    private Integer type;

    private String typeName;

    private Integer relatedId;

    private Integer relatedType;

    private String relatedTypeName;

    private Integer isRead;

    private Date createTime;

    private Date readTime;
}
