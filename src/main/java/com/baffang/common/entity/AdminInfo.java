package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

@Data
public class AdminInfo {
    private Integer id;
    private String username;
    private String name;
    private String password;
    private Integer type;
    private String avatar;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
    private Date lastLoginTime;
}
