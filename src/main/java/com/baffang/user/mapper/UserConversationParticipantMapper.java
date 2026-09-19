package com.baffang.user.mapper;

import com.baffang.common.entity.ConversationParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户端对话参与者Mapper
 */
@Mapper
public interface UserConversationParticipantMapper {

    /**
     * 插入对话参与者
     *
     * @param conversationParticipant 对话参与者实体
     * @return 影响行数
     */
    int insert(ConversationParticipant conversationParticipant);

    /**
     * 根据对话ID查询参与者列表
     *
     * @param conversationId 对话ID
     * @return 参与者列表
     */
    List<ConversationParticipant> selectByConversationId(@Param("conversationId") Integer conversationId);
}