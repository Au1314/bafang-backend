package com.baffang.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@ApiModel("充电站信息")
public class ChargingStationVO {
    
    @ApiModelProperty("充电站ID")
    private String id;
    
    @ApiModelProperty("名称")
    private String name;
    
    @ApiModelProperty("地址")
    private String address;
    
    @ApiModelProperty("经度")
    private Double longitude;
    
    @ApiModelProperty("纬度")
    private Double latitude;
    
    @ApiModelProperty("距离（公里）")
    private Double distance;
    
    @ApiModelProperty("充电费用信息")
    private String chargeFee;
    
    @ApiModelProperty("空闲桩数")
    private Integer availablePiles;
    
    @ApiModelProperty("总桩数")
    private Integer totalPiles;
    
    @ApiModelProperty("营业时间")
    private String businessHours;
    
    @ApiModelProperty("联系电话")
    private String tel;
    
    @ApiModelProperty("类型")
    private String type;
    
    @ApiModelProperty("停车费")
    private String parkingFee;
    
    // ========== 新增字段 ==========
    
    @ApiModelProperty("POI类型分类")
    private String poiType;
    
    @ApiModelProperty("用户评分（满分5分）")
    private Double rating;
    
    @ApiModelProperty("营业时间段")
    private String openTime;
    
    @ApiModelProperty("省份")
    private String province;
    
    @ApiModelProperty("城市")
    private String city;
    
    @ApiModelProperty("区县")
    private String district;
    
    @ApiModelProperty("照片URL列表")
    private List<String> photos;
}