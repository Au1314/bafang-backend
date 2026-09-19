package com.baffang.admin.dto;

import lombok.Data;
import java.util.Date;

@Data
public class AdminRegisterResultDTO {
    private Integer id;
    private String username;
    private String name;
    private String avatar;
    private Integer type;
    private String typeName;
    private Date createTime;
}
