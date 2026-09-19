package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class VehicleAuditListVO {
    
    private Integer id;
    
    private String auditNo;
    
    private Integer businessId;
    
    private Integer applicantId;
    
    private String applicantName;
    
    private String applicantPhone;
    
    /**
     * 车牌号
     */
    private String vehicleNumber;
    
    /**
     * 车名
     */
    private String carName;
    
    /**
     * 品牌
     */
    private String carBrand;
    
    /**
     * 型号
     */
    private String carType;
    
    /**
     * 当前状态
     */
    private Integer currentStatus;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
