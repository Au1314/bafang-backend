package com.baffang.admin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PrivateStationDetailDTO {
    
    private Integer id;
    
    private String name;
    
    private String locationName;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private BigDecimal price;
    
    private Integer status;
    
    private String statusName;
    
    private Integer type;
    
    private String typeName;
    
    private Integer currentType;
    
    private String currentTypeName;
    
    private Integer maxChargeVoltage;
    
    private Integer maxChargeCurrent;
    
    private Integer maxChargePower;
    
    private Integer maxVehicles;
    
    private Integer currentVehicles;
    
    private Integer openStatus;
    
    private String openStatusName;
    
    private Date openTime;
    
    private Date closeTime;
    
    private Integer authStatus;
    
    private String authStatusName;
    
    private String image;
    
    private String description;
    
    private BigDecimal rating;
    
    private Integer ownerId;
    
    private String ownerName;
    
    private String ownerPhone;
    
    private String ownerAvatar;
    
    private Date createTime;
    
    private Date updateTime;

    // ========== 新增字段 ==========
    /**
     * 定价类型：0-固定价格，1-分时定价
     */
    private Integer priceType;
    
    /**
     * 定价类型名称
     */
    private String priceTypeName;
}
