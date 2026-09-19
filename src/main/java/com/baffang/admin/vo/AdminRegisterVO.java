package com.baffang.admin.vo;

import lombok.Data;
import java.util.Date;

@Data
public class AdminRegisterVO {
    private Integer id;
    private String username;
    private String name;
    private String avatar;
    private Integer type;
    private String typeName;
    private Date createTime;
}
