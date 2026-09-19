package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StationAuthDetailVO {
    
    // ========== 原有字段 ==========
    private Integer id;
    private String stationName;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String description;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    
    // 所有者信息
    private Integer ownerId;
    private String ownerUsername;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private String ownerAvatar;
    private Integer ownerAuthVehicle;
    private String ownerAuthVehicleName;
    private Integer ownerAuthChargingStation;
    private String ownerAuthChargingStationName;
    
    // ========== 新增审核记录信息 ==========
    private AuditInfo auditInfo;
    
    @Data
    public static class AuditInfo {
        private Integer auditId;
        private String auditNo;
        private Integer currentStatus;
        private String currentStatusName;
        private Integer priority;
        private String priorityName;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date submitTime;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date finishTime;
        
        private String finalOpinion;
        private String rejectReason;
        
        /**
         * 审核操作日志
         */
        private List<AuditLogInfo> logs;
    }
    
    @Data
    public static class AuditLogInfo {
        private Integer action;
        private String actionName;
        private Integer fromStatus;
        private String fromStatusName;
        private Integer toStatus;
        private String toStatusName;
        private String opinion;
        private String adminName;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;
    }
}
