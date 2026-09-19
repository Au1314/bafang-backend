package com.baffang.user.service;

import com.baffang.common.result.PageResult;
import com.baffang.user.dto.EmergencyCallDTO;
import com.baffang.user.dto.V2VCallDTO;
import com.baffang.user.dto.V2VResponseDTO;
import com.baffang.user.vo.CallListVO;
import com.baffang.user.vo.CallResponseVO;
import com.baffang.user.vo.EmergencyCallListVO;

import java.util.Map;

/**
 * 呼叫服务接口
 * 处理V2V充电呼叫相关业务逻辑
 */
public interface CallService {

        /**
         * 发起V2V充电呼叫
         * 
         * @param userId     用户ID
         * @param v2vCallDTO 呼叫请求参数
         */
        void initiateV2VCall(Integer userId, V2VCallDTO v2vCallDTO);

        /**
         * 取消V2V充电呼叫
         * 将用户所有待响应（0）和已响应（1）的呼叫设为已取消（3）
         * 
         * @param userId 用户ID
         */
        void cancelV2VCall(Integer userId);

        /**
         * 查询半径内所有待响应呼叫
         * 
         * @param currentLng 当前经度
         * @param currentLat 当前纬度
         * @param radius     搜索半径（米）
         * @return 待响应呼叫列表和总数
         */
        Map<String, Object> searchPendingCallsByRadius(Double currentLng, Double currentLat, Integer radius);

        /**
         * 响应V2V充电呼叫
         * 
         * @param userId         响应用户ID
         * @param callId         呼叫ID
         * @param v2vResponseDTO 响应请求参数
         */
        void responseV2VCall(Integer userId, Integer callId, V2VResponseDTO v2vResponseDTO);

        /**
         * 创建与V2V呼叫者的对话
         * 
         * @param userId 响应用户ID
         * @param callId 呼叫ID
         */
        void createConversationForV2VCall(Integer userId, Integer callId);

        /**
         * 查询V2V呼叫记录
         * 
         * @param userId   用户ID
         * @param role     角色：0 = 发起者，1 = 响应者（可选）
         * @param status   状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消（可选）
         * @param pageNum  页码（默认1）
         * @param pageSize 每页条数（默认10）
         * @return 分页的呼叫记录列表
         */
        PageResult<CallListVO> queryV2VCallRecords(Integer userId, Integer role, Integer status, Integer pageNum,
                        Integer pageSize);

        /**
         * 发起紧急救援呼叫
         * 
         * @param userId           用户ID
         * @param emergencyCallDTO 紧急呼叫请求参数
         */
        void initiateEmergencyCall(Integer userId, EmergencyCallDTO emergencyCallDTO);

        /**
         * 取消紧急救援呼叫
         * 将用户所有待响应（0）和已响应（1）的紧急呼叫设为已取消（3）
         * 
         * @param userId 用户ID
         */
        void cancelEmergencyCall(Integer userId);

        /**
         * 查询紧急呼叫记录
         * 
         * @param userId   用户ID
         * @param callType 呼叫类型：0 = 拖车，1 = 充电桩，2 = 其他（可选）
         * @param status   状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消（可选）
         * @param pageNum  页码（默认1）
         * @param pageSize 每页条数（默认10）
         * @return 分页的紧急呼叫记录列表
         */
        PageResult<EmergencyCallListVO> queryEmergencyCallRecords(Integer userId, Integer callType, Integer status,
                        Integer pageNum,
                        Integer pageSize);

        /**
         * 查询单个紧急呼叫进度信息
         * 
         * @param userId 用户ID（呼叫发起者）
         * @return 紧急呼叫进度信息
         */
        EmergencyCallListVO queryEmergencyCallProgress(Integer userId);
}
