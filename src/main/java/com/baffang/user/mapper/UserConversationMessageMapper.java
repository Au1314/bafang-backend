package com.baffang.user.mapper;

import com.baffang.common.entity.ConversationMessage;
import com.baffang.user.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户端对话消息Mapper
 */
@Mapper
public interface UserConversationMessageMapper {

    /**
     * 插入对话消息
     *
     * @param conversationMessage 对话消息实体
     * @return 影响行数
     */
    int insert(ConversationMessage conversationMessage);

    /**
     * 根据对话ID查询消息列表
     *
     * @param conversationId 对话ID
     * @return 消息列表
     */
    List<MessageVO> selectMessagesByConversationId(@Param("conversationId") Integer conversationId);
}