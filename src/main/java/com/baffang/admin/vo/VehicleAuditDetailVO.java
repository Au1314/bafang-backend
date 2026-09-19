package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class VehicleAuditDetailVO {
    
    /**
     * 审核信息
     */
    private AuditInfo auditInfo;
    
    /**
     * 申请人信息
     */
    private ApplicantInfo applicantInfo;
    
    /**
     * 车辆信息
     */
    private VehicleInfo vehicleInfo;
    
    /**
     * 审核日志
     */
    private List<AuditLogInfo> auditLogs;
    
    @Data
    public static class AuditInfo {
        private Integer id;
        private String auditNo;
        private Integer currentStatus;
        private Integer priority;
        private String title;
        private String content;
        private String attachments;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date submitTime;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date finishTime;
        
        private Integer currentAdminId;
        private String currentAdminName;
        private String finalOpinion;
        private String rejectReason;
    }
    
    @Data
    public static class ApplicantInfo {
        private Integer id;
        private String name;
        private String phone;
        private String avatar;
        private Integer authVehicle;
    }
    
    @Data
    public static class VehicleInfo {
        private Integer id;
        private String vehicleNumber;
        private String carName;
        private String carBrand;
        private String carType;
        private String vehicleType;
        
        /**
         * 0：直流电，1：交流电
         */
        private Integer currentType;
        
        /**
         * 最大充电电压（伏特）
         */
        private Integer maxChargeVoltage;
        
        /**
         * 最大充电电流（安培）
         */
        private Integer maxChargeCurrent;
        
        /**
         * 最大充电功率（瓦）
         */
        private Integer maxChargePower;
        
        /**
         * 电池容量（千瓦时）
         */
        private BigDecimal batteryCapacity;
        
        /**
         * 放电功率（瓦）
         */
        private Integer dischargePower;
        
        /**
         * 续航（公里）
         */
        private Integer range;
        
        /**
         * 认证状态
         */
        private Integer authStatus;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;
    }
    
    @Data
    public static class AuditLogInfo {
        private Integer id;
        private Integer adminId;
        private String adminName;
        private Integer action;
        private String actionText;
        private Integer fromStatus;
        private Integer toStatus;
        private String opinion;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;
    }
}
