package com.baffang.admin.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrivateStationUpdateDTO {
    
    private String name;
    
    private String locationName;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private BigDecimal price;
    
    private Integer status;
    
    private Integer type;
    
    private Integer currentType;
    
    private Integer maxChargeVoltage;
    
    private Integer maxChargeCurrent;
    
    private Integer maxChargePower;
    
    private Integer openStatus;
    
    private String image;
    
    private String description;

     // ========== 新增字段 ==========
    /**
     * 定价类型：0-固定价格，1-分时定价
     */
    private Integer priceType;
}
