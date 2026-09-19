package com.baffang.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("充电站查询请求")
public class ChargingStationRequest {
    
    @ApiModelProperty(value = "经度", required = true, example = "116.397390")
    private Double longitude;
    
    @ApiModelProperty(value = "纬度", required = true, example = "39.908860")
    private Double latitude;
    
    @ApiModelProperty(value = "搜索半径（米）", example = "5000")
    private Integer radius = 5000;
    
    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;
    
    @ApiModelProperty(value = "每页数量", example = "20")
    private Integer pageSize = 20;
}