package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.AiChatResult;
import com.baffang.user.dto.PrivateStationAuthDTO;
import com.baffang.user.service.PrivateStationService;
import com.baffang.user.vo.PrivateStationInfoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 修改私桩信息工具类
 */
public class UpdatePrivateStationTools extends AbstractToolWithNotification {

    private final PrivateStationService privateStationService;

    public UpdatePrivateStationTools(PrivateStationService privateStationService, Integer userId,
            WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.privateStationService = privateStationService;
    }

    /**
     * 修改用户充电桩信息
     * 
     * @param stationId        充电桩ID（必须）
     * @param name             私桩名称（可为null，null则不修改）
     * @param locationName     地点名称（可为null，null则不修改）
     * @param longitude        经度（可为null，null则不修改）
     * @param latitude         纬度（可为null，null则不修改）
     * @param price            基础充电价格（元/度，可为null，null则不修改）
     * @param type             私桩类型：0：普通私桩，1：专业私桩（可为null，null则不修改）
     * @param currentType      电流类型：0：直流电，1：交流电（可为null，null则不修改）
     * @param maxChargeVoltage 最大充电电压（单位：伏特，可为null，null则不修改）
     * @param maxChargeCurrent 最大充电电流（单位：安培，可为null，null则不修改）
     * @param maxChargePower   最大充电功率（单位：瓦，可为null，null则不修改）
     * @param maxVehicles      最大车辆数（可为null，null则不修改）
     * @param image            认证附件URL（可为null，null则不修改）
     * @param description      私桩描述（可为null，null则不修改）
     * @return 修改结果
     */
    @Tool(description = "修改用户充电桩信息，需传入对应充电桩id，工具会验证该充电桩是否属于当前登录用户，该工具会自动为用户展示充电桩修改过后的信息，因此你无需重复描述。支持修改的字段：- name: 私桩名称；- locationName: 地点名称；- longitude: 经度；- latitude: 纬度；- price: 基础充电价格（元/度）；- type: 私桩类型（0：普通私桩，1：专业私桩）；- currentType: 电流类型（0：直流电，1：交流电）；- maxChargeVoltage: 最大充电电压（单位：伏特）；- maxChargeCurrent: 最大充电电流（单位：安培）；- maxChargePower: 最大充电功率（单位：瓦）；- maxVehicles: 最大车辆数；- image: 认证附件URL（多个URL用逗号分隔）；- description: 私桩描述。所有参数可设置为null，如果参数为null则不修改对应字段。")
    public String updatePrivateStation(
            @ToolParam(description = "充电桩ID（必须）") Integer stationId,
            @ToolParam(description = "私桩名称（可为null，null则不修改）") String name,
            @ToolParam(description = "地点名称（可为null，null则不修改）") String locationName,
            @ToolParam(description = "经度（可为null，null则不修改）") Double longitude,
            @ToolParam(description = "纬度（可为null，null则不修改）") Double latitude,
            @ToolParam(description = "基础充电价格（元/度，可为null，null则不修改）") Double price,
            @ToolParam(description = "私桩类型：0：普通私桩，1：专业私桩（可为null，null则不修改）") Integer type,
            @ToolParam(description = "电流类型：0：直流电，1：交流电（可为null，null则不修改）") Integer currentType,
            @ToolParam(description = "最大充电电压（单位：伏特，可为null，null则不修改）") Integer maxChargeVoltage,
            @ToolParam(description = "最大充电电流（单位：安培，可为null，null则不修改）") Integer maxChargeCurrent,
            @ToolParam(description = "最大充电功率（单位：瓦，可为null，null则不修改）") Integer maxChargePower,
            @ToolParam(description = "最大车辆数（可为null，null则不修改）") Integer maxVehicles,
            @ToolParam(description = "认证附件URL（可为null，null则不修改）") String image,
            @ToolParam(description = "私桩描述（可为null，null则不修改）") String description) {

        // 发送工具调用通知
        sendToolCallNotification("updatePrivateStation", "修改用户充电桩信息");

        // 1. 使用从构造函数传入的userId
        Integer userId = super.userId;
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 2. 参数验证
        if (stationId == null) {
            throw new BusinessException("充电桩ID不能为空");
        }

        // 3. 创建PrivateStationAuthDTO对象
        PrivateStationAuthDTO privateStationAuthDTO = new PrivateStationAuthDTO();
        privateStationAuthDTO.setName(name);
        privateStationAuthDTO.setLocationName(locationName);
        privateStationAuthDTO.setLongitude(longitude);
        privateStationAuthDTO.setLatitude(latitude);
        privateStationAuthDTO.setPrice(price);
        privateStationAuthDTO.setType(type);
        privateStationAuthDTO.setCurrentType(currentType);
        privateStationAuthDTO.setMaxChargeVoltage(maxChargeVoltage);
        privateStationAuthDTO.setMaxChargeCurrent(maxChargeCurrent);
        privateStationAuthDTO.setMaxChargePower(maxChargePower);
        privateStationAuthDTO.setMaxVehicles(maxVehicles);
        privateStationAuthDTO.setImage(image);
        privateStationAuthDTO.setDescription(description);

        // 4. 调用Service修改私桩信息
        privateStationService.resubmitPrivateStationAuth(userId, stationId, privateStationAuthDTO);

        // 5. 获取修改后的私桩信息
        PrivateStationInfoVO privateStationInfoVO = privateStationService.getPrivateStationInfo(userId, stationId);

        // 6. 发送修改后的私桩信息给用户
        sendToolReturnNotification("updatePrivateStation", privateStationInfoVO);

        // 8. 返回结果
        return "私桩信息修改成功";
    }
}