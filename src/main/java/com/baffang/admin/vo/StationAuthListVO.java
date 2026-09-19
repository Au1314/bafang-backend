package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StationAuthListVO {
    
    private Integer id;
    private String stationName;
    private String address;
    private Integer status;
    private String statusName;
    private Integer isPublic;
    private Integer chargerCount;
    private BigDecimal defaultPrice;
     // ========== 新增字段 ==========
    /**
     * 定价类型：0-固定价格，1-分时定价
     */
    private Integer priceType;
    
    /**
     * 定价类型名称
     */
    private String priceTypeName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    // 所有者信息
    private Integer ownerId;
    private String ownerUsername;
    private String ownerName;
    private String ownerPhone;
    
    // 认证状态（从用户表获取）
    private Integer authStatus;
    private String authStatusName;
    
    // ========== 新增审核记录信息 ==========
    private String auditNo;
    private Integer auditStatus;
    private String auditStatusName;
}
