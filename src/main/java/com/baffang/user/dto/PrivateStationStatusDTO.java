package com.baffang.user.dto;

import lombok.Data;

/**
 * 私桩状态设置DTO
 */
@Data
public class PrivateStationStatusDTO {
    /**
     * 开关状态，0=关闭，1=开启
     */
    private Integer openStatus;
}