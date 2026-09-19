package com.baffang.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("private_charge_station")
public class PrivateChargeStation {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String location;

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

    private Integer maxVehicles;

    private Integer currentVehicles;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDeleted;

    private Integer openStatus;

    private Date openTime;

    private Date closeTime;

    private Integer ownerId;

    private Integer authStatus;

    private String image;

    private String description;

    private Integer priceType;

    private BigDecimal rating;
}
