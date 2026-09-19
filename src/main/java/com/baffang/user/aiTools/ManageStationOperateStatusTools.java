package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.exception.BusinessException;
import com.baffang.user.dto.PrivateStationDynamicPriceDTO;
import com.baffang.user.dto.PrivateStationDynamicPriceDTO.DynamicPriceRuleDTO;
import com.baffang.user.dto.PrivateStationTimeConfigDTO;
import com.baffang.user.service.PrivateStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理充电桩运营状态工具类
 */
public class ManageStationOperateStatusTools extends AbstractToolWithNotification {

    private final PrivateStationService privateStationService;

    public ManageStationOperateStatusTools(PrivateStationService privateStationService, Integer userId,
            WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.privateStationService = privateStationService;
    }

    /**
     * 管理充电桩运营状态
     * 
     * @param stationId             充电桩ID（必须）
     * @param fixedPrice            基础充电价格（元/度，可为null，null则不修改）
     * @param priceType             价格类型：0-静态价格，1-动态价格（可为null，null则不修改）
     * @param openStatus            开关状态：0=关闭，1=开启（可为null，null则不修改）
     * @param openTime              每日开启时间（格式：HH:mm:ss，例如：08:00:00，可为null，null则不修改）
     * @param closeTime             每日关闭时间（格式：HH:mm:ss，例如：22:00:00，可为null，null则不修改）
     * @param dynamicRulesBeginTime 动态价格规则开始时间列表（格式：HH:mm:ss，可为null，null则不修改动态价格规则）
     * @param dynamicRulesEndTime   动态价格规则结束时间列表（格式：HH:mm:ss，与dynamicRulesBeginTime一一对应，可为null，null则不修改动态价格规则）
     * @param dynamicRulesPrice     动态价格规则价格列表（单位：元/度，与dynamicRulesBeginTime一一对应，可为null，null则不修改动态价格规则）
     * @return 管理结果
     */
    @Tool(description = "管理充电桩运营状态，需传入对应充电桩id，工具会验证该充电桩是否属于当前登录用户。支持配置的字段：- fixedPrice: 基础充电价格（元/度）；- priceType: 价格类型（0-静态价格，1-动态价格）；- openStatus: 开关状态（0=关闭，1=开启）；- openTime: 每日开启时间（格式：HH:mm:ss，例如：08:00:00）；- closeTime: 每日关闭时间（格式：HH:mm:ss，例如：22:00:00）；- dynamicRulesBeginTime, dynamicRulesEndTime, dynamicRulesPrice: 动态价格规则（开始时间、结束时间、价格需一一对应，例如：[\"08:00:00\",\"18:00:00\"]、[\"18:00:00\",\"22:00:00\"]、[1.5,2.0]）。所有参数可设置为null，如果参数为null则不修改对应字段。")
    public String manageStationOperateStatus(
            @ToolParam(description = "充电桩ID（必须）") Integer stationId,
            @ToolParam(description = "基础充电价格（元/度，可为null，null则不修改）") Double fixedPrice,
            @ToolParam(description = "价格类型：0-静态价格，1-动态价格（可为null，null则不修改）") Integer priceType,
            @ToolParam(description = "开关状态：0=关闭，1=开启（可为null，null则不修改）") Integer openStatus,
            @ToolParam(description = "每日开启时间（格式：HH:mm:ss，例如：08:00:00，可为null，null则不修改）") String openTime,
            @ToolParam(description = "每日关闭时间（格式：HH:mm:ss，例如：22:00:00，可为null，null则不修改）") String closeTime,
            @ToolParam(description = "动态价格规则开始时间列表（格式：HH:mm:ss，可为null，null则不修改动态价格规则）") List<String> dynamicRulesBeginTime,
            @ToolParam(description = "动态价格规则结束时间列表（格式：HH:mm:ss，与dynamicRulesBeginTime一一对应，可为null，null则不修改动态价格规则）") List<String> dynamicRulesEndTime,
            @ToolParam(description = "动态价格规则价格列表（单位：元/度，与dynamicRulesBeginTime一一对应，可为null，null则不修改动态价格规则）") List<Double> dynamicRulesPrice) {
        // 发送工具调用通知
        sendToolCallNotification("manageStationOperateStatus", "管理充电桩运营状态");

        // 1. 参数验证
        Integer userId = super.userId; // 使用从构造函数传入的userId
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 5. 参数验证
        if (stationId == null) {
            throw new BusinessException("充电桩ID不能为空");
        }

        // 6. 执行相应的操作
        boolean hasUpdated = false;
        StringBuilder resultMsg = new StringBuilder("充电桩运营状态管理：");

        // 6.1 设置基础充电价格
        if (fixedPrice != null) {
            privateStationService.setPrivateStationFixedPrice(userId, stationId, fixedPrice);
            resultMsg.append("基础充电价格已更新为 ").append(fixedPrice).append(" 元/度；");
            hasUpdated = true;
        }

        // 6.2 设置价格类型（静态/动态）
        if (priceType != null) {
            if (priceType != 0 && priceType != 1) {
                throw new BusinessException("无效的价格类型，必须为0或1");
            }
            privateStationService.setPrivateStationPriceType(userId, stationId, priceType);
            resultMsg.append("价格类型已设置为 ").append(priceType == 0 ? "静态价格" : "动态价格").append("；");
            hasUpdated = true;
        }

        // 6.3 设置开关状态
        if (openStatus != null) {
            if (openStatus != 0 && openStatus != 1) {
                throw new BusinessException("无效的开关状态，必须为0或1");
            }
            privateStationService.setPrivateStationStatus(userId, stationId, openStatus);
            resultMsg.append("充电桩已").append(openStatus == 1 ? "开启" : "关闭").append("；");
            hasUpdated = true;
        }

        // 6.4 设置启停时间
        if (openTime != null || closeTime != null) {
            PrivateStationTimeConfigDTO timeConfigDTO = new PrivateStationTimeConfigDTO();
            timeConfigDTO.setOpenTime(openTime);
            timeConfigDTO.setCloseTime(closeTime);
            privateStationService.setPrivateStationTimeConfig(userId, stationId, timeConfigDTO);
            resultMsg.append("启停时间已更新");
            if (openTime != null)
                resultMsg.append("（开启时间：").append(openTime).append("）");
            if (closeTime != null)
                resultMsg.append("（关闭时间：").append(closeTime).append("）");
            resultMsg.append("；");
            hasUpdated = true;
        }

        // 6.5 设置动态价格规则
        if (dynamicRulesBeginTime != null && dynamicRulesEndTime != null && dynamicRulesPrice != null) {
            if (dynamicRulesBeginTime.size() != dynamicRulesEndTime.size() ||
                    dynamicRulesBeginTime.size() != dynamicRulesPrice.size()) {
                throw new BusinessException("动态价格规则的开始时间、结束时间和价格列表长度必须一致");
            }

            PrivateStationDynamicPriceDTO dynamicPriceDTO = new PrivateStationDynamicPriceDTO();
            List<DynamicPriceRuleDTO> rules = new ArrayList<>();

            for (int i = 0; i < dynamicRulesBeginTime.size(); i++) {
                DynamicPriceRuleDTO rule = new DynamicPriceRuleDTO();
                rule.setBeginTime(dynamicRulesBeginTime.get(i));
                rule.setEndTime(dynamicRulesEndTime.get(i));
                rule.setPrice(dynamicRulesPrice.get(i));
                rules.add(rule);
            }

            dynamicPriceDTO.setRules(rules);
            privateStationService.setPrivateStationDynamicPrice(userId, stationId, dynamicPriceDTO);
            resultMsg.append("动态价格规则已更新（共").append(rules.size()).append("条规则）；");
            hasUpdated = true;
        }

        if (!hasUpdated) {
            resultMsg.append("未更新任何字段");
        }

        // 7. 返回结果
        return resultMsg.toString();
    }
}