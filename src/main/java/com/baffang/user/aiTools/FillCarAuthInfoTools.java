package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.user.dto.CarAuthDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 帮助用户填写车辆认证信息工具类
 */
public class FillCarAuthInfoTools extends AbstractToolWithNotification {

    public FillCarAuthInfoTools(Integer userId, WebSocketSessionManager webSocketSessionManager,
            ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
    }

    @Tool(description = "帮助用户填写车辆认证信息，但车牌号字段如果用户未告知则不进行填写。该工具会自动将传入参数整理后返回给用户，由用户在前端进行酌情修改后再提交认证，因此你无需再对参数进行重复描述。注意：当前平台在开发阶段，如果不知道用户告诉你车型的具体参数，可酌情编造\n"
            +
            "枚举字段说明：\n" +
            "- currentType: 用电类型（0：直流电，1：交流电）")
    public void fillCarAuthInfo(@ToolParam(description = "车辆类型") String vehicleType,
            @ToolParam(description = "用电类型（0：直流电，1：交流电）") Integer currentType,
            @ToolParam(description = "品牌") String carBrand,
            @ToolParam(description = "型号") String carType,
            @ToolParam(description = "最大充电电压") Integer maxChargeVoltage,
            @ToolParam(description = "最大充电电流") Integer maxChargeCurrent,
            @ToolParam(description = "最大充电功率") Integer maxChargePower,
            @ToolParam(description = "电池容量") Double batteryCapacity,
            @ToolParam(description = "放电功率") Integer dischargePower,
            @ToolParam(description = "续航") Integer range,
            @ToolParam(description = "车辆号码（可选，用户未告知则不填写）") String vehicleNumber) {
        // 发送工具调用通知
        sendToolCallNotification("fillCarAuthInfo", "帮助用户填写车辆认证信息");

        // 创建CarAuthDTO对象
        CarAuthDTO carAuthDTO = new CarAuthDTO();
        carAuthDTO.setVehicleType(vehicleType);
        carAuthDTO.setCurrentType(currentType);
        carAuthDTO.setCarBrand(carBrand);
        carAuthDTO.setCarType(carType);
        carAuthDTO.setMaxChargeVoltage(maxChargeVoltage);
        carAuthDTO.setMaxChargeCurrent(maxChargeCurrent);
        carAuthDTO.setMaxChargePower(maxChargePower);
        carAuthDTO.setBatteryCapacity(batteryCapacity);
        carAuthDTO.setDischargePower(dischargePower);
        carAuthDTO.setRange(range);

        // 如果车牌号不为null且不为空，则设置车牌号
        if (vehicleNumber != null && !vehicleNumber.trim().isEmpty()) {
            carAuthDTO.setVehicleNumber(vehicleNumber);
        }

        // 发送工具返回通知，将整理后的参数返回前端
        sendToolReturnNotification("fillCarAuthInfo", carAuthDTO);
    }
}