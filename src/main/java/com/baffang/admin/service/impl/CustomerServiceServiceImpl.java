package com.baffang.admin.service.impl;

import com.baffang.admin.dto.*;
import com.baffang.common.entity.*;
import com.baffang.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baffang.admin.mapper.*;
import com.baffang.admin.service.CustomerServiceService;
import com.baffang.admin.vo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceServiceImpl implements CustomerServiceService {
    
    private final AiCustomerServiceConfigMapper aiConfigMapper;
    private final ConversationMapper conversationMapper;
    private final ConversationParticipantMapper participantMapper;
    private final ConversationMessageMapper messageMapper;
    private final AdminMapper adminMapper;
    private final ObjectMapper objectMapper;
    
    // ==================== 日期转换工具方法 ====================
    private Date convertLocalDateTimeToDate(Object dateObj) {
        if (dateObj == null) {
            return null;
        }
        if (dateObj instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) dateObj;
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } else if (dateObj instanceof Date) {
            return (Date) dateObj;
        } else {
            return null;
        }
    }
    
    // ==================== AI配置管理 ====================
    
    @Override
    public AiConfigVO getAiConfig() {
        List<AiCustomerServiceConfig> configs = aiConfigMapper.selectAllConfigs();
        Map<String, String> configMap = configs.stream()
                .collect(Collectors.toMap(AiCustomerServiceConfig::getConfigKey, 
                        c -> c.getConfigValue() != null ? c.getConfigValue() : ""));
        
        AiConfigVO vo = new AiConfigVO();
        vo.setAiEnabled("1".equals(configMap.get("ai_enabled")));
        vo.setAiModel(configMap.get("ai_model"));
        String apiKey = configMap.get("ai_api_key");
        if (apiKey != null && apiKey.length() > 8) {
            vo.setAiApiKey(apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4));
        } else {
            vo.setAiApiKey(apiKey);
        }
        vo.setAiApiUrl(configMap.get("ai_api_url"));
        vo.setAiSystemPrompt(configMap.get("ai_system_prompt"));
        vo.setAiMaxTokens(parseInteger(configMap.get("ai_max_tokens"), 500));
        vo.setAiTemperature(parseDouble(configMap.get("ai_temperature"), 0.7));
        vo.setAiWelcomeMessage(configMap.get("ai_welcome_message"));
        vo.setTransferKeywords(parseJsonArray(configMap.get("transfer_keywords")));
        vo.setAutoTransferThreshold(parseInteger(configMap.get("auto_transfer_threshold"), 3));
        vo.setWorkingHoursStart(configMap.get("working_hours_start"));
        vo.setWorkingHoursEnd(configMap.get("working_hours_end"));
        vo.setOfflineMessage(configMap.get("offline_message"));
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAiConfig(AiConfigDTO dto) {
        if (dto.getAiEnabled() != null) {
            aiConfigMapper.updateValueByKey("ai_enabled", dto.getAiEnabled() ? "1" : "0");
        }
        if (dto.getAiModel() != null) {
            aiConfigMapper.updateValueByKey("ai_model", dto.getAiModel());
        }
        if (dto.getAiApiKey() != null && !dto.getAiApiKey().contains("****")) {
            aiConfigMapper.updateValueByKey("ai_api_key", dto.getAiApiKey());
        }
        if (dto.getAiApiUrl() != null) {
            aiConfigMapper.updateValueByKey("ai_api_url", dto.getAiApiUrl());
        }
        if (dto.getAiSystemPrompt() != null) {
            aiConfigMapper.updateValueByKey("ai_system_prompt", dto.getAiSystemPrompt());
        }
        if (dto.getAiMaxTokens() != null) {
            aiConfigMapper.updateValueByKey("ai_max_tokens", String.valueOf(dto.getAiMaxTokens()));
        }
        if (dto.getAiTemperature() != null) {
            aiConfigMapper.updateValueByKey("ai_temperature", String.valueOf(dto.getAiTemperature()));
        }
        if (dto.getAiWelcomeMessage() != null) {
            aiConfigMapper.updateValueByKey("ai_welcome_message", dto.getAiWelcomeMessage());
        }
        if (dto.getTransferKeywords() != null) {
            try {
                aiConfigMapper.updateValueByKey("transfer_keywords", 
                        objectMapper.writeValueAsString(dto.getTransferKeywords()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("转人工关键词格式错误");
            }
        }
        if (dto.getAutoTransferThreshold() != null) {
            aiConfigMapper.updateValueByKey("auto_transfer_threshold", 
                    String.valueOf(dto.getAutoTransferThreshold()));
        }
        if (dto.getWorkingHoursStart() != null) {
            aiConfigMapper.updateValueByKey("working_hours_start", dto.getWorkingHoursStart());
        }
        if (dto.getWorkingHoursEnd() != null) {
            aiConfigMapper.updateValueByKey("working_hours_end", dto.getWorkingHoursEnd());
        }
        if (dto.getOfflineMessage() != null) {
            aiConfigMapper.updateValueByKey("offline_message", dto.getOfflineMessage());
        }
    }
    
    // ==================== 人工客服对话 ====================
    
    @Override
    public PageInfo<PendingConversationVO> getPendingConversations(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Map<String, Object>> page = conversationMapper.selectPendingConversations();
        
        List<PendingConversationVO> list = page.getResult().stream().map(map -> {
            PendingConversationVO vo = new PendingConversationVO();
            vo.setId((Integer) map.get("id"));
            vo.setUserId((Integer) map.get("userId"));
            vo.setUserName((String) map.get("userName"));
            vo.setUserPhone(maskPhone((String) map.get("userPhone")));
            vo.setUserAvatar((String) map.get("userAvatar"));
            vo.setWaitingSeconds(((Number) map.get("waitingSeconds")).longValue());
            vo.setLastMessage((String) map.get("lastMessage"));
            vo.setMessageCount(((Number) map.get("messageCount")).intValue());
            vo.setCreateTime(convertLocalDateTimeToDate(map.get("createTime")));
            return vo;
        }).collect(Collectors.toList());
        
        PageInfo<PendingConversationVO> pageInfo = new PageInfo<>();
        pageInfo.setList(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }
    @Override
public PageInfo<ConversationListVO> getMyConversations(Integer pageNum, Integer pageSize, Integer status, Integer adminId) {
    PageHelper.startPage(pageNum, pageSize);
    List<ConversationListVO> list = conversationMapper.selectMyConversations(adminId, status);
    return new PageInfo<>(list);
}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinConversation(Integer conversationId, Integer adminId) {
        // 1. 检查对话是否存在且为进行中
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getIsDeleted() == 1) {
            throw new BusinessException("对话不存在");
        }
        if (conversation.getStatus() != 1) {
            throw new BusinessException("对话已结束");
        }
        if (conversation.getType() != 0) {
            throw new BusinessException("非客服对话");
        }
        
        // 2. 检查是否已有客服接入（改用自定义方法）
        ConversationParticipant existAdmin = participantMapper.selectAdminParticipant(conversationId);
        if (existAdmin != null) {
            throw new BusinessException("该对话已有客服接入");
        }
        
        // 3. 添加客服为参与者
        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversationId(conversationId);
        participant.setParticipantId(adminId);
        participant.setParticipantType(1);
        participant.setRole(1);
        participant.setJoinTime(new Date());
        participant.setLastReadTime(new Date());
        participant.setUnreadCount(0);
        participant.setIsDeleted(0);
        participantMapper.insert(participant);
        
        // 4. 发送系统消息
        AdminInfo admin = adminMapper.selectById(adminId.longValue());
        String adminName = admin != null ? admin.getName() : "客服";
        
        ConversationMessage sysMessage = new ConversationMessage();
        sysMessage.setConversationId(conversationId);
        sysMessage.setSenderId(adminId);
        sysMessage.setSenderType(2);
        sysMessage.setContent("客服 " + adminName + " 已接入对话");
        sysMessage.setMessageType(3);
        sysMessage.setSendTime(new Date());
        sysMessage.setIsRead(0);
        sysMessage.setIsDeleted(0);
        messageMapper.insert(sysMessage);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageVO sendMessage(Integer conversationId, SendMessageDTO dto, Integer adminId) {
        // 1. 检查对话
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getIsDeleted() == 1) {
            throw new BusinessException("对话不存在");
        }
        if (conversation.getStatus() != 1) {
            throw new BusinessException("对话已结束");
        }
        
        // 2. 检查客服是否是该对话的参与者（改用自定义方法）
        ConversationParticipant participant = participantMapper.selectByConversationAndParticipant(
                conversationId, adminId, 1);
        if (participant == null) {
            throw new BusinessException("您不是该对话的参与者");
        }
        
        // 3. 保存消息
        ConversationMessage message = new ConversationMessage();
        message.setConversationId(conversationId);
        message.setSenderId(adminId);
        message.setSenderType(1);
        message.setContent(dto.getContent());
        message.setMessageType(dto.getMessageType() != null ? dto.getMessageType() : 0);
        message.setExtraInfo(dto.getExtraInfo());
        message.setSendTime(new Date());
        message.setIsRead(0);
        message.setIsDeleted(0);
        messageMapper.insert(message);
        
        // 4. 更新用户的未读消息数（改用自定义方法）
        participantMapper.incrementUnreadCount(conversationId, 0);
        
        // 5. 更新对话时间
        conversation.setUpdateTime(new Date());
        conversationMapper.updateById(conversation);
        
        // 6. 返回消息VO
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setConversationId(conversationId);
        vo.setSenderId(adminId);
        vo.setSenderType(1);
        vo.setSenderTypeName("管理员");
        AdminInfo admin = adminMapper.selectById(adminId.longValue());
        vo.setSenderName(admin != null ? admin.getName() : "客服");
        vo.setSenderAvatar(admin != null ? admin.getAvatar() : null);
        vo.setContent(dto.getContent());
        vo.setMessageType(message.getMessageType());
        vo.setMessageTypeName(getMessageTypeName(message.getMessageType()));
        vo.setExtraInfo(dto.getExtraInfo());
        vo.setSendTime(message.getSendTime());
        vo.setIsRead(0);
        
        return vo;
    }
    
    @Override
    public PageInfo<MessageVO> getMessages(Integer conversationId, Integer pageNum, Integer pageSize, Integer adminId) {
        // 1. 检查对话是否存在
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getIsDeleted() == 1) {
            throw new BusinessException("对话不存在");
        }
        
        // 2. 查询消息
        PageHelper.startPage(pageNum, pageSize);
        Page<Map<String, Object>> page = messageMapper.selectMessagesByConversationId(conversationId);
        
        List<MessageVO> list = page.getResult().stream().map(map -> {
            MessageVO vo = new MessageVO();
            vo.setId((Integer) map.get("id"));
            vo.setConversationId((Integer) map.get("conversationId"));
            vo.setSenderId((Integer) map.get("senderId"));
            vo.setSenderType((Integer) map.get("senderType"));
            vo.setSenderTypeName(getSenderTypeName((Integer) map.get("senderType")));
            vo.setSenderName((String) map.get("senderName"));
            vo.setSenderAvatar((String) map.get("senderAvatar"));
            vo.setContent((String) map.get("content"));
            vo.setMessageType((Integer) map.get("messageType"));
            vo.setMessageTypeName(getMessageTypeName((Integer) map.get("messageType")));
            vo.setExtraInfo((String) map.get("extraInfo"));
            vo.setSendTime(convertLocalDateTimeToDate(map.get("sendTime")));
            vo.setIsRead((Integer) map.get("isRead"));
            return vo;
        }).collect(Collectors.toList());
        
        // 3. 更新客服的未读消息数和最后阅读时间（改用自定义方法）
        participantMapper.updateReadStatus(conversationId, adminId, 1, new Date());
        
        // 4. 标记消息为已读（用户发送的消息）（改用自定义方法）
        messageMapper.markAsRead(conversationId, 0);
        
        PageInfo<MessageVO> pageInfo = new PageInfo<>();
        pageInfo.setList(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeConversation(Integer conversationId, Integer adminId) {
        // 1. 检查对话
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getIsDeleted() == 1) {
            throw new BusinessException("对话不存在");
        }
        if (conversation.getStatus() != 1) {
            throw new BusinessException("对话已结束");
        }
        
        // 2. 更新对话状态
        conversation.setStatus(3);
        conversation.setEndTime(new Date());
        conversation.setUpdateTime(new Date());
        conversationMapper.updateById(conversation);
        
        // 3. 发送系统消息
        AdminInfo admin = adminMapper.selectById(adminId.longValue());
        String adminName = admin != null ? admin.getName() : "客服";
        
        ConversationMessage sysMessage = new ConversationMessage();
        sysMessage.setConversationId(conversationId);
        sysMessage.setSenderId(adminId);
        sysMessage.setSenderType(2);
        sysMessage.setContent("客服 " + adminName + " 已结束对话");
        sysMessage.setMessageType(3);
        sysMessage.setSendTime(new Date());
        sysMessage.setIsRead(0);
        sysMessage.setIsDeleted(0);
        messageMapper.insert(sysMessage);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferConversation(Integer conversationId, TransferConversationDTO dto, Integer adminId) {
        // 1. 检查对话
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getIsDeleted() == 1) {
            throw new BusinessException("对话不存在");
        }
        if (conversation.getStatus() != 1) {
            throw new BusinessException("对话已结束");
        }
        
        // 2. 检查当前客服是否是参与者（改用自定义方法）
        ConversationParticipant currentParticipant = participantMapper.selectByConversationAndParticipant(
                conversationId, adminId, 1);
        if (currentParticipant == null) {
            throw new BusinessException("您不是该对话的参与者");
        }
        
        // 3. 检查目标客服
        AdminInfo targetAdmin = adminMapper.selectById(dto.getTargetAdminId().longValue());
        if (targetAdmin == null) {
            throw new BusinessException("目标客服不存在");
        }
        
        // 4. 移除当前客服（逻辑删除）
        currentParticipant.setIsDeleted(1);
        participantMapper.updateById(currentParticipant);
        
        // 5. 添加新客服
        ConversationParticipant newParticipant = new ConversationParticipant();
        newParticipant.setConversationId(conversationId);
        newParticipant.setParticipantId(dto.getTargetAdminId());
        newParticipant.setParticipantType(1);
        newParticipant.setRole(1);
        newParticipant.setJoinTime(new Date());
        newParticipant.setLastReadTime(new Date());
        newParticipant.setUnreadCount(0);
        newParticipant.setIsDeleted(0);
        participantMapper.insert(newParticipant);
        
        // 6. 发送系统消息
        AdminInfo fromAdmin = adminMapper.selectById(adminId.longValue());
        String fromName = fromAdmin != null ? fromAdmin.getName() : "客服";
        String toName = targetAdmin.getName();
        String reason = dto.getReason() != null ? "（原因：" + dto.getReason() + "）" : "";
        
        ConversationMessage sysMessage = new ConversationMessage();
        sysMessage.setConversationId(conversationId);
        sysMessage.setSenderId(adminId);
        sysMessage.setSenderType(2);
        sysMessage.setContent("客服 " + fromName + " 已将对话转接给 " + toName + reason);
        sysMessage.setMessageType(3);
        sysMessage.setSendTime(new Date());
        sysMessage.setIsRead(0);
        sysMessage.setIsDeleted(0);
        messageMapper.insert(sysMessage);
        
        // 7. 更新对话时间
        conversation.setUpdateTime(new Date());
        conversationMapper.updateById(conversation);
    }
    
    // ==================== 对话记录管理 ====================
    
    @Override
    public PageInfo<ConversationListVO> getConversationList(ConversationQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Map<String, Object>> page = conversationMapper.selectConversationList(queryDTO);
        
        List<ConversationListVO> list = page.getResult().stream().map(map -> {
            ConversationListVO vo = new ConversationListVO();
            vo.setId((Integer) map.get("id"));
            vo.setStatus((Integer) map.get("status"));
            vo.setStatusName(getStatusName((Integer) map.get("status")));
            vo.setUserId((Integer) map.get("userId"));
            vo.setUserName((String) map.get("userName"));
            vo.setUserPhone(maskPhone((String) map.get("userPhone")));
            vo.setUserAvatar((String) map.get("userAvatar"));
            vo.setAdminId((Integer) map.get("adminId"));
            vo.setAdminName((String) map.get("adminName"));
            vo.setLastMessage((String) map.get("lastMessage"));
            vo.setLastMessageTime(convertLocalDateTimeToDate(map.get("lastMessageTime")));
            vo.setUnreadCount(map.get("unreadCount") != null ? ((Number) map.get("unreadCount")).intValue() : 0);
            vo.setMessageCount(map.get("messageCount") != null ? ((Number) map.get("messageCount")).intValue() : 0);
            vo.setCreateTime(convertLocalDateTimeToDate(map.get("createTime")));
            vo.setEndTime(convertLocalDateTimeToDate(map.get("endTime")));
            return vo;
        }).collect(Collectors.toList());
        
        PageInfo<ConversationListVO> pageInfo = new PageInfo<>();
        pageInfo.setList(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }
    
    @Override
    public ConversationDetailVO getConversationDetail(Integer conversationId) {
        Map<String, Object> map = conversationMapper.selectConversationDetail(conversationId);
        if (map == null) {
            throw new BusinessException("对话不存在");
        }
        
        ConversationDetailVO vo = new ConversationDetailVO();
        vo.setId((Integer) map.get("id"));
        vo.setStatus((Integer) map.get("status"));
        vo.setStatusName(getStatusName((Integer) map.get("status")));
        vo.setUserId((Integer) map.get("userId"));
        vo.setUserName((String) map.get("userName"));
        vo.setUserPhone(maskPhone((String) map.get("userPhone")));
        vo.setUserAvatar((String) map.get("userAvatar"));
        vo.setAdminId((Integer) map.get("adminId"));
        vo.setAdminName((String) map.get("adminName"));
        vo.setCreateTime(convertLocalDateTimeToDate(map.get("createTime")));
        vo.setEndTime(convertLocalDateTimeToDate(map.get("endTime")));
        vo.setRemark((String) map.get("remark"));
        
        // 查询参与者
        List<Map<String, Object>> participants = participantMapper.selectParticipantsByConversationId(conversationId);
        List<ParticipantVO> participantVOs = participants.stream().map(p -> {
            ParticipantVO pvo = new ParticipantVO();
            pvo.setParticipantId((Integer) p.get("participantId"));
            pvo.setParticipantType((Integer) p.get("participantType"));
            pvo.setParticipantTypeName(getSenderTypeName((Integer) p.get("participantType")));
            pvo.setRole((Integer) p.get("role"));
            pvo.setRoleName(getRoleName((Integer) p.get("role")));
            pvo.setName((String) p.get("name"));
            pvo.setJoinTime(convertLocalDateTimeToDate(p.get("joinTime")));
            return pvo;
        }).collect(Collectors.toList());
        vo.setParticipants(participantVOs);
        
        return vo;
    }
    
    // ==================== 辅助方法 ====================
    
    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "进行中";
            case 2: return "已完成";
            case 3: return "已关闭";
            default: return "未知";
        }
    }
    
    private String getSenderTypeName(Integer senderType) {
        if (senderType == null) return "";
        switch (senderType) {
            case 0: return "用户";
            case 1: return "管理员";
            case 2: return "系统";
            default: return "未知";
        }
    }
    
    private String getMessageTypeName(Integer messageType) {
        if (messageType == null) return "";
        switch (messageType) {
            case 0: return "文本";
            case 1: return "图片";
            case 2: return "语音";
            case 3: return "系统通知";
            default: return "未知";
        }
    }
    
    private String getRoleName(Integer role) {
        if (role == null) return "";
        switch (role) {
            case 0: return "发起者";
            case 1: return "响应者";
            case 2: return "观察者";
            default: return "未知";
        }
    }
    
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
    
    private Integer parseInteger(String value, Integer defaultValue) {
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private Double parseDouble(String value, Double defaultValue) {
        try {
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private List<String> parseJsonArray(String json) {
        try {
            if (json == null || json.isEmpty()) return new ArrayList<>();
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }


    @Override
    public CustomerServiceStatsVO getStatsOverview(Integer adminId) {
        CustomerServiceStatsVO stats = new CustomerServiceStatsVO();
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        
        // 待接入对话数
        stats.setPendingCount(conversationMapper.countByStatus(0));
        
        // 当前进行中对话数（当前客服的）
        stats.setOngoingCount(conversationMapper.countOngoingByAdminId(adminId));
        
        // 今日接待数
        stats.setTodayCount(conversationMapper.countTodayByAdminId(adminId, todayStart, todayEnd));
        
        // 今日已完成数
        stats.setTodayCompletedCount(conversationMapper.countTodayCompletedByAdminId(adminId, todayStart, todayEnd));
        
        // 今日消息数 - 改成 messageMapper
        stats.setTodayMessageCount(messageMapper.countTodayByAdminId(adminId, todayStart, todayEnd));
        
        // 本周接待数
        LocalDateTime weekStart = today.with(DayOfWeek.MONDAY).atStartOfDay();
        stats.setWeekCount(conversationMapper.countByAdminIdAndTimeRange(adminId, weekStart, todayEnd));
        
        // 本月接待数
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        stats.setMonthCount(conversationMapper.countByAdminIdAndTimeRange(adminId, monthStart, todayEnd));
        
        // 平均响应时间
        stats.setAvgResponseTime(0);
        
        return stats;
    }
    
    @Override
    public AdminStatsVO getMyStats(Integer adminId) {
        AdminStatsVO stats = new AdminStatsVO();
        stats.setAdminId(adminId);
        
        // 获取客服名称 - 改成 adminMapper
        AdminInfo admin = adminMapper.selectById(adminId.longValue());
        if (admin != null) {
            stats.setAdminName(admin.getName());
        }
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        
        // 今日接待数
        stats.setTodayCount(conversationMapper.countTodayByAdminId(adminId, todayStart, todayEnd));
        
        // 今日完成数
        stats.setTodayCompletedCount(conversationMapper.countTodayCompletedByAdminId(adminId, todayStart, todayEnd));
        
        // 当前进行中
        stats.setOngoingCount(conversationMapper.countOngoingByAdminId(adminId));
        
        // 今日消息数 - 改成 messageMapper
        stats.setMessageCount(messageMapper.countTodayByAdminId(adminId, todayStart, todayEnd));
        
        stats.setAvgResponseTime(0);
        
        return stats;
    }
    
    @Override
    public List<AdminStatsVO> getAllAdminStats() {
        // 获取所有客服人员 - 使用新添加的方法
        List<AdminInfo> admins = adminMapper.selectCustomerServiceAdmins();
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        
        List<AdminStatsVO> result = new ArrayList<>();
        for (AdminInfo admin : admins) {
            AdminStatsVO stats = new AdminStatsVO();
            stats.setAdminId(admin.getId());
            stats.setAdminName(admin.getName());
            stats.setTodayCount(conversationMapper.countTodayByAdminId(admin.getId(), todayStart, todayEnd));
            stats.setTodayCompletedCount(conversationMapper.countTodayCompletedByAdminId(admin.getId(), todayStart, todayEnd));
            stats.setOngoingCount(conversationMapper.countOngoingByAdminId(admin.getId()));
            stats.setMessageCount(messageMapper.countTodayByAdminId(admin.getId(), todayStart, todayEnd));
            stats.setAvgResponseTime(0);
            result.add(stats);
        }
        
        return result;
    }
    

    

}
