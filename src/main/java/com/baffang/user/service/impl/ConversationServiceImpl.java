package com.baffang.user.service.impl;

import com.baffang.common.entity.Conversation;
import com.baffang.common.entity.ConversationMessage;
import com.baffang.common.entity.ConversationParticipant;
import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.common.entity.UserInfo;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.enums.entityEnum.ConversationStatusEnum;
import com.baffang.common.enums.entityEnum.ConversationTypeEnum;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.user.dto.SendMessageDTO;
import com.baffang.user.mapper.UserConversationMapper;
import com.baffang.user.mapper.UserConversationMessageMapper;
import com.baffang.user.mapper.UserConversationParticipantMapper;
import com.baffang.user.mapper.UserMapper;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.service.ConversationService;
import com.baffang.user.vo.ConversationCreateVO;
import com.baffang.user.vo.ConversationListVO;
import com.baffang.user.vo.MessageVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 对话服务实现类
 */
@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private UserConversationMapper conversationMapper;

    @Autowired
    private UserConversationParticipantMapper conversationParticipantMapper;

    @Autowired
    private UserConversationMessageMapper conversationMessageMapper;

    @Autowired
    private UserPrivateChargeStationMapper privateChargeStationMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationCreateVO createConversationWithStationOwner(Integer userId, Integer stationId) {
        // 1. 根据私桩ID查询私桩信息
        PrivateChargeStation privateChargeStation = privateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null) {
            throw new BusinessException(ErrorMsg.PRIVATE_STATION_NOT_EXIST);
        }

        Integer ownerId = privateChargeStation.getOwnerId();
        String locationName = privateChargeStation.getLocationName();
        String stationName = privateChargeStation.getName();

        // 2. 查询桩主信息
        UserInfo ownerInfo = userMapper.selectById(ownerId);
        if (ownerInfo == null) {
            throw new BusinessException(ErrorMsg.OWNER_NOT_EXIST);
        }

        String ownerName = ownerInfo.getName() != null && !ownerInfo.getName().isEmpty() ? ownerInfo.getName()
                : ownerInfo.getPhone();

        // 3. 创建对话记录
        Conversation conversation = new Conversation();
        conversation.setType(ConversationTypeEnum.RESERVATION.getCode()); // 1-预约对话
        conversation.setStatus(ConversationStatusEnum.IN_PROGRESS.getCode()); // 1-进行中

        conversationMapper.insertConversation(conversation);
        Integer conversationId = conversation.getId();

        // 4. 添加对话参与者：预约者
        ConversationParticipant participant1 = new ConversationParticipant();
        participant1.setConversationId(conversationId);
        participant1.setParticipantId(userId);
        participant1.setParticipantType(0); // 0-普通用户
        participant1.setRole(0); // 0-发起者

        conversationParticipantMapper.insert(participant1);

        // 5. 添加对话参与者：桩主
        ConversationParticipant participant2 = new ConversationParticipant();
        participant2.setConversationId(conversationId);
        participant2.setParticipantId(ownerId);
        participant2.setParticipantType(0); // 0-普通用户
        participant2.setRole(1); // 1-响应者

        conversationParticipantMapper.insert(participant2);

        // 6. 自动添加初始消息："您好，我想预约您在{locationName}的{name}充电桩。"
        ConversationMessage initialMessage = new ConversationMessage();
        initialMessage.setConversationId(conversationId);
        initialMessage.setSenderId(userId);
        initialMessage.setSenderType(0); // 0-普通用户
        initialMessage.setContent(String.format("您好，我想预约您在%s的%s充电桩。", locationName, stationName));
        initialMessage.setMessageType(0); // 0-文本

        conversationMessageMapper.insert(initialMessage);

        // 7. 构建并返回响应VO
        ConversationCreateVO vo = new ConversationCreateVO();
        vo.setConversationId(conversationId);
        vo.setStationId(stationId);
        vo.setOwnerId(ownerId);
        vo.setOwnerName(ownerName);

        return vo;
    }

    @Override
    public Result<PageResult<ConversationListVO>> queryConversationList(Integer userId, Integer pageNum,
            Integer pageSize) {
        // 1. 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 2. 查询对话列表
        List<ConversationListVO> conversationList = conversationMapper.selectConversationListByUserId(userId);
        PageInfo<ConversationListVO> pageInfo = new PageInfo<>(conversationList);

        // 3. 构建分页结果
        PageResult<ConversationListVO> pageResult = new PageResult<>();
        pageResult.setTotal((int) pageInfo.getTotal());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setList(pageInfo.getList());

        // 4. 返回成功结果
        return Result.success(pageResult, "查询成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> sendMessage(Integer userId, Integer conversationId, SendMessageDTO sendMessageDTO) {
        // 1. 验证用户是否是对话参与者
        List<ConversationParticipant> participants = conversationParticipantMapper
                .selectByConversationId(conversationId);
        boolean isParticipant = false;
        for (ConversationParticipant participant : participants) {
            if (participant.getParticipantId().equals(userId)) {
                isParticipant = true;
                break;
            }
        }
        if (!isParticipant) {
            throw new BusinessException(ErrorMsg.NOT_CONVERSATION_PARTICIPANT);
        }

        // 2. 创建消息对象
        ConversationMessage message = new ConversationMessage();
        message.setConversationId(conversationId);
        message.setSenderId(userId);
        message.setSenderType(0); // 0-普通用户
        message.setContent(sendMessageDTO.getContent());
        message.setMessageType(sendMessageDTO.getMessageType() != null ? sendMessageDTO.getMessageType() : 0); // 默认文本消息
        message.setExtraInfo(sendMessageDTO.getExtraInfo());

        // 3. 插入消息
        conversationMessageMapper.insert(message);

        // 4. 构建返回结果
        java.util.Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap.put("messageId", message.getId());
        resultMap.put("sendTime", message.getSendTime());

        // 5. 返回成功结果
        return Result.success(resultMap, "消息发送成功");
    }

    @Override
    public Result<PageResult<?>> queryConversationMessages(Integer userId, Integer conversationId, Integer pageNum,
            Integer pageSize) {
        // 1. 验证用户是否是对话参与者
        List<ConversationParticipant> participants = conversationParticipantMapper
                .selectByConversationId(conversationId);
        boolean isParticipant = false;
        for (ConversationParticipant participant : participants) {
            if (participant.getParticipantId().equals(userId)) {
                isParticipant = true;
                break;
            }
        }
        if (!isParticipant) {
            throw new BusinessException(ErrorMsg.NOT_CONVERSATION_PARTICIPANT);
        }

        // 2. 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 3. 查询消息列表
        List<MessageVO> messages = conversationMessageMapper.selectMessagesByConversationId(conversationId);

        // 4. 处理whoSend字段
        for (MessageVO message : messages) {
            // 0-当前用户发送，1-对方发送
            message.setWhoSend(message.getSenderId().equals(userId) ? 0 : 1);
        }

        PageInfo<MessageVO> pageInfo = new PageInfo<>(messages);

        // 5. 构建分页结果
        PageResult<MessageVO> pageResult = new PageResult<>();
        pageResult.setTotal((int) pageInfo.getTotal());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setPageNum(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setList(pageInfo.getList());

        // 6. 返回成功结果
        return Result.success(pageResult, "查询成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> endConversation(Integer userId, Integer conversationId) {
        // 1. 验证用户是否是对话参与者
        List<ConversationParticipant> participants = conversationParticipantMapper
                .selectByConversationId(conversationId);
        boolean isParticipant = false;
        for (ConversationParticipant participant : participants) {
            if (participant.getParticipantId().equals(userId)) {
                isParticipant = true;
                break;
            }
        }
        if (!isParticipant) {
            throw new BusinessException(ErrorMsg.NOT_CONVERSATION_PARTICIPANT);
        }

        // 2. 更新对话状态为已关闭
        int rows = conversationMapper.updateConversationStatus(conversationId, ConversationStatusEnum.CLOSED.getCode());
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.CONVERSATION_NOT_FOUND);
        }

        // 3. 自动添加结束对话消息："[自动]我已结束对话"
        ConversationMessage endMessage = new ConversationMessage();
        endMessage.setConversationId(conversationId);
        endMessage.setSenderId(userId);
        endMessage.setSenderType(0); // 0-普通用户
        endMessage.setContent("[自动]我已结束对话");
        endMessage.setMessageType(0); // 0-文本

        conversationMessageMapper.insert(endMessage);

        // 4. 返回成功结果
        return Result.success(null, "对话已结束");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> initiateManualServiceConsultation(Integer userId, String content) {
        // 1. 创建客服对话记录
        Conversation conversation = new Conversation();
        conversation.setType(ConversationTypeEnum.CUSTOMER_SERVICE.getCode()); // 0-客服对话
        conversation.setStatus(ConversationStatusEnum.IN_PROGRESS.getCode()); // 1-进行中
        conversation.setRemark("待客服响应"); // 设置备注为待客服响应

        conversationMapper.insertConversation(conversation);
        Integer conversationId = conversation.getId();

        // 2. 添加对话参与者：用户
        ConversationParticipant userParticipant = new ConversationParticipant();
        userParticipant.setConversationId(conversationId);
        userParticipant.setParticipantId(userId);
        userParticipant.setParticipantType(0); // 0-普通用户
        userParticipant.setRole(0); // 0-发起者

        conversationParticipantMapper.insert(userParticipant);

        // 3. 添加对话参与者：系统（客服代表）
        ConversationParticipant systemParticipant = new ConversationParticipant();
        systemParticipant.setConversationId(conversationId);
        systemParticipant.setParticipantId(0); // 0-系统用户
        systemParticipant.setParticipantType(2); // 2-系统
        systemParticipant.setRole(1); // 1-响应者

        conversationParticipantMapper.insert(systemParticipant);

        // 4. 创建初始咨询消息
        ConversationMessage initialMessage = new ConversationMessage();
        initialMessage.setConversationId(conversationId);
        initialMessage.setSenderId(userId);
        initialMessage.setSenderType(0); // 0-普通用户
        initialMessage.setContent(content);
        initialMessage.setMessageType(0); // 0-文本

        conversationMessageMapper.insert(initialMessage);

        // 5. 返回成功结果
        return Result.success(null, "人工客服会话创建成功，客服人员将尽快响应");
    }
}