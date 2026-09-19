package com.baffang.user.dto;

import lombok.Data;

import java.util.Date;

/**
 * 私桩预约请求DTO
 */
@Data
public class PrivateStationReserveDTO {
    /**
     * 预约时间
     */
    private String reserveTime;
}