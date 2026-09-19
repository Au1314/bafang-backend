package com.baffang.user.aiTools;

import com.baffang.admin.websocket.WebSocketSessionManager;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.user.service.PrivateStationService;
import com.baffang.user.vo.PrivateStationListVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

/**
 * 私人充电桩信息工具类
 */
public class PrivateStationTools extends AbstractToolWithNotification {

    private final PrivateStationService privateStationService;

    public PrivateStationTools(PrivateStationService privateStationService, Integer userId,
            WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        super(userId, webSocketSessionManager, objectMapper);
        this.privateStationService = privateStationService;
    }

    /**
     * 查询当前登录用户的私人充电桩列表
     * 
     * @return 私人充电桩列表，包含id、名称、位置、认证状态、开放状态和评分等信息
     * 
     *         枚举字段说明：
     *         - authStatus: 认证状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败
     *         - openStatus: 开放状态，0: 关闭, 1: 开启
     */
    @Tool(description = "查询当前登录用户的私人充电桩列表，返回包含id、名称、位置、认证状态、开放状态和评分等信息的列表。枚举字段说明：- authStatus: 认证状态，0: 未认证, 1: 审核中, 2: 已认证, 3: 认证失败；- openStatus: 开放状态，0: 关闭, 1: 开启")
    public List<PrivateStationListVO> getCurrentUserPrivateStationList() {
        // 发送工具调用通知
        sendToolCallNotification("getCurrentUserPrivateStationList", "查询当前登录用户的私人充电桩列表");

        // 使用从构造函数传入的userId
        Integer userId = super.userId;
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 调用PrivateStationService获取私桩列表，使用默认的分页参数（第1页，每页100条，确保获取所有私桩）
        PageResult<PrivateStationListVO> pageResult = privateStationService.getPrivateStationListByUserId(userId, 1,
                100);

        // 返回私桩列表
        return pageResult.getList();
    }
}
