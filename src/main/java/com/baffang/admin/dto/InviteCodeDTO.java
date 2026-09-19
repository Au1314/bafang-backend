package com.baffang.admin.dto;

import lombok.Data;
import java.util.Date;

@Data
public class InviteCodeDTO {
    private Integer id;
    private String code;
    private Integer targetType;
    private String targetTypeName;
    private Integer maxUses;
    private Integer status;
    private String statusName;
    private Integer usedCount;
    private Date expireTime;
    private Date createTime;
}
