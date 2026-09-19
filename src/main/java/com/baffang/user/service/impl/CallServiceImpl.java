package com.baffang.user.service.impl;

import com.baffang.common.entity.Conversation;
import com.baffang.common.entity.ConversationMessage;
import com.baffang.common.entity.ConversationParticipant;
import com.baffang.common.entity.EmergencyCall;
import com.baffang.common.entity.VToV;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.enums.entityEnum.ConversationMessageTypeEnum;
import com.baffang.common.enums.entityEnum.ConversationParticipantRoleEnum;
import com.baffang.common.enums.entityEnum.ConversationStatusEnum;
import com.baffang.common.enums.entityEnum.ConversationTypeEnum;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.util.GeoUtils;
import com.baffang.common.util.GeoUtils.GeoPoint;
import com.baffang.user.dto.EmergencyCallDTO;
import com.baffang.user.dto.V2VCallDTO;
import com.baffang.user.dto.V2VResponseDTO;
import com.baffang.user.mapper.UserCallMapper;
import com.baffang.user.vo.CallListVO;
import com.baffang.user.vo.EmergencyCallListVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.baffang.user.mapper.UserConversationMapper;
import com.baffang.user.mapper.UserConversationMessageMapper;
import com.baffang.user.mapper.UserConversationParticipantMapper;
import com.baffang.user.mapper.UserMapper;
import com.baffang.user.service.CallService;
import com.baffang.user.vo.CallResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 呼叫服务实现类
 */
@Service
public class CallServiceImpl implements CallService {

    @Autowired
    private UserCallMapper userCallMapper;

    @Autowired
    private UserConversationMapper conversationMapper;

    @Autowired
    private UserConversationParticipantMapper conversationParticipantMapper;

    @Autowired
    private UserConversationMessageMapper conversationMessageMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 最大呼叫范围（公里）
     */
    private static final int MAX_CALL_RANGE = 10;

    /**
     * 最小呼叫范围（公里）
     */
    private static final int MIN_CALL_RANGE = 1;

    /**
     * 最大呼叫位置描述长度
     */
    private static final int MAX_CALL_POSITION_LENGTH = 200;

    /**
     * 最小呼叫位置描述长度
     */
    private static final int MIN_CALL_POSITION_LENGTH = 1;

    /**
     * 呼叫状态：待响应
     */
    private static final int CALL_STATUS_PENDING = 0;

    /**
     * 呼叫状态：已响应
     */
    private static final int CALL_STATUS_RESPONDED = 1;

    /**
     * 默认搜索半径（公里）
     */
    private static final int DEFAULT_RADIUS = 3;

    /**
     * 最大搜索半径（公里）
     */
    private static final int MAX_RADIUS = 10;

    /**
     * 经纬度范围扩展值（用于数据库初步筛选）
     */
    private static final double LNG_LAT_RANGE = 0.3;

    @Override
    public void initiateV2VCall(Integer userId, V2VCallDTO v2vCallDTO) {
        // 参数验证
        validateV2VCallParams(v2vCallDTO);

        // 检查用户是否已有活跃中的呼叫
        if (userId != null) {
            int activeCallCount = userCallMapper.countActiveCallsByUserId(userId);
            if (activeCallCount > 0) {
                throw new BusinessException(ErrorMsg.ACTIVE_CALL_EXISTS);
            }
        }

        // 创建V2V呼叫记录
        VToV vToV = new VToV();
        vToV.setRequestUserId(userId);
        vToV.setResponseUserId(null); // 初始为空，等待响应
        vToV.setCallRange(v2vCallDTO.getCallRange().doubleValue());
        vToV.setCallTime(new Date());
        vToV.setCallPosition(v2vCallDTO.getCallPosition());
        vToV.setTargetPosition(null); // 初始为空，等待协商
        vToV.setCallLongitude(v2vCallDTO.getLongitude());
        vToV.setCallLatitude(v2vCallDTO.getLatitude());
        vToV.setTargetLongitude(null); // 初始为空，等待协商
        vToV.setTargetLatitude(null); // 初始为空，等待协商
        vToV.setStatus(CALL_STATUS_PENDING); // 待响应状态
        vToV.setCreateTime(new Date());
        vToV.setUpdateTime(new Date());

        // 保存到数据库
        userCallMapper.insertV2VCall(vToV);
    }

    /**
     * 验证V2V呼叫请求参数
     * 
     * @param v2vCallDTO 呼叫请求参数
     */
    private void validateV2VCallParams(V2VCallDTO v2vCallDTO) {
        // 验证呼叫范围
        Integer callRange = v2vCallDTO.getCallRange();
        if (callRange == null || callRange < MIN_CALL_RANGE || callRange > MAX_CALL_RANGE) {
            throw new IllegalArgumentException("呼叫范围必须在0到10公里之间");
        }

        // 验证呼叫位置描述
        String callPosition = v2vCallDTO.getCallPosition();
        if (callPosition == null || callPosition.length() < MIN_CALL_POSITION_LENGTH
                || callPosition.length() > MAX_CALL_POSITION_LENGTH) {
            throw new IllegalArgumentException("呼叫位置描述长度必须在1到200字符之间");
        }

        // 验证经度
        Double longitude = v2vCallDTO.getLongitude();
        if (longitude == null || !GeoUtils.isValidLongitude(longitude)) {
            throw new IllegalArgumentException("经度必须在-180到180之间");
        }

        // 验证纬度
        Double latitude = v2vCallDTO.getLatitude();
        if (latitude == null || !GeoUtils.isValidLatitude(latitude)) {
            throw new IllegalArgumentException("纬度必须在-90到90之间");
        }
    }

    @Override
    public void cancelV2VCall(Integer userId) {
        // 将用户所有待响应（0）和已响应（1）的呼叫设为已取消（3）
        userCallMapper.updateCallStatusToCanceled(userId);
    }

    @Override
    public Map<String, Object> searchPendingCallsByRadius(Double currentLng, Double currentLat, Integer radius) {
        // 1. 参数处理
        // 设置默认半径
        if (radius == null || radius <= 0) {
            radius = DEFAULT_RADIUS;
        } else if (radius > MAX_RADIUS) {
            // 限制最大半径
            radius = MAX_RADIUS;
        }

        // 2. 构建数据库查询参数
        Double minLng = null;
        Double maxLng = null;
        Double minLat = null;
        Double maxLat = null;

        if (currentLng != null && currentLat != null) {
            // 计算经纬度范围（±0.3）
            minLng = currentLng - LNG_LAT_RANGE;
            maxLng = currentLng + LNG_LAT_RANGE;
            minLat = currentLat - LNG_LAT_RANGE;
            maxLat = currentLat + LNG_LAT_RANGE;
        }

        // 3. 从数据库查询符合条件的待响应呼叫
        List<CallResponseVO> pendingCallList = userCallMapper.selectPendingCallsByGeoRange(minLng, maxLng, minLat,
                maxLat);

        // 4. 使用GeoUtils进行双向距离筛选
        List<CallResponseVO> filteredCalls = new ArrayList<>();

        if (pendingCallList != null && !pendingCallList.isEmpty()) {
            if (currentLng != null && currentLat != null) {
                // 查询所有呼叫的详细信息，包括call_range
                List<VToV> fullCallList = userCallMapper.selectAllPendingCalls();

                // 构建call_id到call_range的映射
                Map<Integer, Double> callRangeMap = new HashMap<>();
                for (VToV call : fullCallList) {
                    callRangeMap.put(call.getId(), call.getCallRange());
                }

                // 筛选半径范围内的呼叫
                for (CallResponseVO call : pendingCallList) {
                    if (call.getCallLongitude() != null && call.getCallLatitude() != null) {
                        // 计算响应用户位置到呼叫位置的距离（米）
                        double distanceToCallMeters = GeoUtils.calculateDistance(
                                currentLng, currentLat,
                                call.getCallLongitude(), call.getCallLatitude());

                        // 转换为公里单位，与radius和callRange保持一致
                        double distanceToCallKm = distanceToCallMeters / 1000;

                        // 获取呼叫的call_range
                        Double callRange = callRangeMap.get(call.getId());
                        if (callRange != null) {
                            // 检查双向距离：
                            // 1. 响应用户位置到呼叫位置的距离 <= 响应用户的搜索半径（公里）
                            // 2. 呼叫位置到响应用户位置的距离 <= 呼叫的call_range（公里）
                            if (distanceToCallKm <= radius && distanceToCallKm <= callRange) {
                                filteredCalls.add(call);
                            }
                        }
                    }
                }
            } else {
                // 如果没有经纬度参数，直接返回所有查询结果
                filteredCalls = pendingCallList;
            }
        }

        // 5. 构建结果返回
        Map<String, Object> result = new HashMap<>();
        result.put("list", filteredCalls != null ? filteredCalls : new ArrayList<>());
        result.put("total", filteredCalls != null ? filteredCalls.size() : 0);

        return result;
    }

    @Override
    public void responseV2VCall(Integer userId, Integer callId, V2VResponseDTO v2vResponseDTO) {
        // 1. 参数验证
        validateV2VResponseParams(v2vResponseDTO);

        // 2. 检查呼叫是否存在
        VToV vToV = userCallMapper.selectCallById(callId);
        if (vToV == null) {
            throw new BusinessException(ErrorMsg.CALL_NOT_EXIST);
        }

        // 3. 检查呼叫状态是否为待响应
        if (!Objects.equals(vToV.getStatus(), CALL_STATUS_PENDING)) {
            throw new BusinessException(ErrorMsg.CALL_STATUS_INVALID);
        }

        // 4. 检查用户是否是呼叫发起者（不能自己响应自己的呼叫）
        if (Objects.equals(vToV.getRequestUserId(), userId)) {
            throw new BusinessException(ErrorMsg.CANNOT_RESPONSE_OWN_CALL);
        }

        // 5. 更新呼叫记录
        vToV.setResponseUserId(userId);
        vToV.setTargetPosition(v2vResponseDTO.getTargetPosition());
        vToV.setTargetLongitude(v2vResponseDTO.getTargetLongitude());
        vToV.setTargetLatitude(v2vResponseDTO.getTargetLatitude());
        vToV.setStatus(CALL_STATUS_RESPONDED);
        vToV.setUpdateTime(new Date());

        // 6. 保存到数据库
        userCallMapper.updateCallResponse(vToV);
    }

    /**
     * 验证V2V呼叫响应请求参数
     * 
     * @param v2vResponseDTO 响应请求参数
     */
    private void validateV2VResponseParams(V2VResponseDTO v2vResponseDTO) {
        // 验证目标位置描述
        String targetPosition = v2vResponseDTO.getTargetPosition();
        if (targetPosition == null || targetPosition.length() < MIN_CALL_POSITION_LENGTH
                || targetPosition.length() > MAX_CALL_POSITION_LENGTH) {
            throw new IllegalArgumentException("目标位置描述长度必须在1到200字符之间");
        }

        // 验证目标经度
        Double targetLongitude = v2vResponseDTO.getTargetLongitude();
        if (targetLongitude == null || !GeoUtils.isValidLongitude(targetLongitude)) {
            throw new IllegalArgumentException("目标经度必须在-180到180之间");
        }

        // 验证目标纬度
        Double targetLatitude = v2vResponseDTO.getTargetLatitude();
        if (targetLatitude == null || !GeoUtils.isValidLatitude(targetLatitude)) {
            throw new IllegalArgumentException("目标纬度必须在-90到90之间");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createConversationForV2VCall(Integer userId, Integer callId) {
        // 1. 获取呼叫详情
        VToV vToV = userCallMapper.selectCallById(callId);
        if (vToV == null) {
            throw new BusinessException(ErrorMsg.CALL_NOT_EXIST);
        }

        // 2. 检查呼叫是否已响应
        if (!Objects.equals(vToV.getStatus(), CALL_STATUS_RESPONDED)) {
            throw new BusinessException(ErrorMsg.CALL_STATUS_INVALID);
        }

        // 3. 检查当前用户是否是响应用户
        if (!Objects.equals(vToV.getResponseUserId(), userId)) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        // 4. 创建对话
        Conversation conversation = new Conversation();
        conversation.setType(ConversationTypeEnum.V2V.getCode()); // 3-V2V 对话
        conversation.setStatus(ConversationStatusEnum.IN_PROGRESS.getCode()); // 1-进行中

        conversationMapper.insertConversation(conversation);
        Integer conversationId = conversation.getId();

        // 5. 添加呼叫者作为参与者（发起者）
        ConversationParticipant callerParticipant = new ConversationParticipant();
        callerParticipant.setConversationId(conversationId);
        callerParticipant.setParticipantId(vToV.getRequestUserId());
        callerParticipant.setParticipantType(0); // 0-普通用户
        callerParticipant.setRole(ConversationParticipantRoleEnum.INITIATOR.getCode()); // 0-发起者

        conversationParticipantMapper.insert(callerParticipant);

        // 6. 添加响应用户作为参与者（响应者）
        ConversationParticipant responderParticipant = new ConversationParticipant();
        responderParticipant.setConversationId(conversationId);
        responderParticipant.setParticipantId(userId);
        responderParticipant.setParticipantType(0); // 0-普通用户
        responderParticipant.setRole(ConversationParticipantRoleEnum.RESPONDER.getCode()); // 1-响应者

        conversationParticipantMapper.insert(responderParticipant);

        // 7. 发送自动消息：响应者 -> 呼叫者
        String messageContent = String.format("你好，我看到你在%s发起了呼叫，不知可否在%s见面？",
                vToV.getCallPosition(), vToV.getTargetPosition());

        ConversationMessage autoMessage = new ConversationMessage();
        autoMessage.setConversationId(conversationId);
        autoMessage.setSenderId(userId);
        autoMessage.setSenderType(0); // 0-普通用户
        autoMessage.setContent(messageContent);
        autoMessage.setMessageType(ConversationMessageTypeEnum.TEXT.getCode()); // 0-文本

        conversationMessageMapper.insert(autoMessage);
    }

    @Override
    public PageResult<CallListVO> queryV2VCallRecords(Integer userId, Integer role, Integer status, Integer pageNum,
            Integer pageSize) {
        // 设置默认分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询呼叫记录
        List<CallListVO> records = userCallMapper.selectCallRecords(userId, role, status);

        // 转换为PageResult
        PageInfo<CallListVO> pageInfo = new PageInfo<>(records);
        PageResult<CallListVO> pageResult = new PageResult<>();
        pageResult.setTotal((int) pageInfo.getTotal());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setList(pageInfo.getList());
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);

        return pageResult;
    }

    @Override
    public void initiateEmergencyCall(Integer userId, EmergencyCallDTO emergencyCallDTO) {
        // 参数验证
        validateEmergencyCallParams(emergencyCallDTO);

        // 检查用户是否已有活跃中的紧急呼叫
        if (userId != null) {
            int activeCallCount = userCallMapper.countActiveEmergencyCallsByUserId(userId);
            if (activeCallCount > 0) {
                throw new BusinessException(ErrorMsg.ACTIVE_CALL_EXISTS);
            }
        }

        // 创建紧急呼叫记录
        EmergencyCall emergencyCall = new EmergencyCall();
        emergencyCall.setRequestUserId(userId);
        emergencyCall.setType(emergencyCallDTO.getCallType());
        emergencyCall.setResponseAdminId(null); // 初始为空，等待管理员响应
        emergencyCall.setCallRange(emergencyCallDTO.getCallRange().doubleValue());
        emergencyCall.setCallTime(new Date());
        emergencyCall.setCallPosition(emergencyCallDTO.getCallPosition());
        emergencyCall.setCallLongitude(emergencyCallDTO.getLongitude());
        emergencyCall.setCallLatitude(emergencyCallDTO.getLatitude());
        emergencyCall.setTargetPosition(null); // 初始为空，等待协商
        emergencyCall.setTargetLongitude(null); // 初始为空，等待协商
        emergencyCall.setTargetLatitude(null); // 初始为空，等待协商
        emergencyCall.setStatus(CALL_STATUS_PENDING); // 待响应状态
        emergencyCall.setCreateTime(new Date());
        emergencyCall.setUpdateTime(new Date());

        // 保存到数据库
        userCallMapper.insertEmergencyCall(emergencyCall);
    }

    /**
     * 验证紧急呼叫请求参数
     * 
     * @param emergencyCallDTO 紧急呼叫请求参数
     */
    private void validateEmergencyCallParams(EmergencyCallDTO emergencyCallDTO) {
        // 1. 验证呼叫类型
        if (emergencyCallDTO.getCallType() == null || emergencyCallDTO.getCallType() < 0
                || emergencyCallDTO.getCallType() > 2) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "呼叫类型无效，必须为0（呼叫拖车）、1（呼叫充电桩）或2（其他呼叫）");
        }

        // 2. 验证呼叫范围
        if (emergencyCallDTO.getCallRange() == null || emergencyCallDTO.getCallRange() < MIN_CALL_RANGE
                || emergencyCallDTO.getCallRange() > MAX_CALL_RANGE) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(),
                    "呼叫范围无效，必须在" + MIN_CALL_RANGE + "-" + MAX_CALL_RANGE + "公里之间");
        }

        // 3. 验证呼叫位置描述
        if (emergencyCallDTO.getCallPosition() == null || emergencyCallDTO.getCallPosition().trim().isEmpty()) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "呼叫位置描述不能为空");
        }
        if (emergencyCallDTO.getCallPosition().length() > MAX_CALL_POSITION_LENGTH) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(),
                    "呼叫位置描述长度不能超过" + MAX_CALL_POSITION_LENGTH + "个字符");
        }

        // 4. 验证经纬度
        if (emergencyCallDTO.getLongitude() == null || emergencyCallDTO.getLongitude() < -180
                || emergencyCallDTO.getLongitude() > 180) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "经度无效，必须在-180到180之间");
        }
        if (emergencyCallDTO.getLatitude() == null || emergencyCallDTO.getLatitude() < -90
                || emergencyCallDTO.getLatitude() > 90) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "纬度无效，必须在-90到90之间");
        }
    }

    @Override
    public void cancelEmergencyCall(Integer userId) {
        // 将用户所有待响应（0）和已响应（1）的紧急呼叫设为已取消（3）
        userCallMapper.updateEmergencyCallStatusToCanceled(userId);
    }

    @Override
    public PageResult<EmergencyCallListVO> queryEmergencyCallRecords(Integer userId, Integer callType, Integer status,
            Integer pageNum,
            Integer pageSize) {
        // 设置默认分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询紧急呼叫记录
        List<EmergencyCallListVO> records = userCallMapper.selectEmergencyCallRecords(userId, callType, status);

        // 转换为PageResult
        PageInfo<EmergencyCallListVO> pageInfo = new PageInfo<>(records);
        PageResult<EmergencyCallListVO> pageResult = new PageResult<>();
        pageResult.setTotal((int) pageInfo.getTotal());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setList(pageInfo.getList());
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);

        return pageResult;
    }

    @Override
    public EmergencyCallListVO queryEmergencyCallProgress(Integer userId) {
        // 参数验证
        if (userId == null) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "用户ID不能为空");
        }

        // 查询紧急呼叫进度
        EmergencyCallListVO emergencyCall = userCallMapper.selectEmergencyCallProgress(userId, null);

        // 检查记录是否存在
        if (emergencyCall == null) {
            throw new BusinessException(ErrorMsg.CALL_NOT_EXIST);
        }

        return emergencyCall;
    }
}
