package com.baffang.admin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PrivateStationListDTO {
    
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
    
    private Integer maxChargePower;
    
    private Integer openStatus;
    
    private String openStatusName;
    
    private Integer authStatus;
    
    private String authStatusName;
    
    private Integer ownerId;
    
    private String ownerName;
    
    private String ownerPhone;
    
    private BigDecimal rating;
    
    private String image;
    
    private Date createTime;


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
