package com.baffang.user.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.baffang.user.vo.PriceConfigVO;

/**
 * 充电私桩信息查询返回参数（用户视角）
 */
@Data
public class ChargingPrivateStationInfoVO {

    /**
     * 私桩ID
     */
    private Integer id;

    /**
     * 私桩名称
     */
    private String name;

    /**
     * 私桩位置
     */
    private String location;

    /**
     * 位置名称
     */
    private String locationName;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 基础充电价格（元/度）
     */
    private BigDecimal price;

    /**
     * 私桩状态
     */
    private Integer status;

    /**
     * 私桩类型：0：普通私桩，1：专业私桩
     */
    private Integer type;

    /**
     * 电流类型：0：直流电，1：交流电
     */
    private Integer currentType;

    /**
     * 最大充电电压，单位：伏特
     */
    private Integer maxChargeVoltage;

    /**
     * 最大充电电流，单位：安培
     */
    private Integer maxChargeCurrent;

    /**
     * 最大充电功率，单位：瓦
     */
    private Integer maxChargePower;

    /**
     * 最大车辆数
     */
    private Integer maxVehicles;

    /**
     * 当前车辆数
     */
    private Integer currentVehicles;

    /**
     * 开放状态：0：关闭，1：开启
     */
    private Integer openStatus;

    /**
     * 每日开启时间
     */
    private Date openTime;

    /**
     * 每日关闭时间
     */
    private Date closeTime;

    /**
     * 演示图片
     */
    private String image;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 价格类型：0-静态价格，1-动态价格
     */
    private Integer priceType;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 动态价格规则列表
     */
    private List<PriceConfigVO.DynamicRuleVO> dynamicRules;
}