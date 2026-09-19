package com.baffang.admin.dto;

import lombok.Data;
import java.util.Date;

/**
 * 管理员信息DTO
 */
@Data
public class AdminInfoDTO {

    private Integer id;

    private String username;

    private String name;

    private String avatar;

    private Integer type;

    private String typeName;

    private Date createTime;

    private Date updateTime;
}
