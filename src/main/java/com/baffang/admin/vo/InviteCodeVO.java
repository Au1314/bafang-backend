package com.baffang.admin.vo;

import lombok.Data;
import java.util.Date;

@Data
public class InviteCodeVO {
    private Integer id;
    private String code;
    private Integer targetType;
    private String targetTypeName;
    private Integer maxUses;
    private Integer usedCount;
    private Date expireTime;
    private Date createTime;
}
