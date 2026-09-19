package com.baffang.user.dto;

import lombok.Data;

/**
 * 处理私桩预约申请请求DTO
 */
@Data
public class HandlePrivateStationReserveDTO {
    /**
     * 操作类型（1=确认，2=拒绝）
     */
    private Integer status;
}