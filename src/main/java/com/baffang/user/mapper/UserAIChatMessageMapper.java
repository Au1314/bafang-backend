package com.baffang.user.mapper;

import java.util.List;

import com.baffang.common.entity.AIChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话消息表Mapper接口
 */
@Mapper
public interface UserAIChatMessageMapper {
    /**
     * 插入AI对话消息
     *
     * @param message AI对话消息
     * @return 插入成功的行数
     */
    int insert(AIChatMessage message);

    /**
     * 查询指定对话的消息历史
     *
     * @param conversationId 对话ID
     * @return 消息历史列表
     */
    List<AIChatMessage> selectMessageHistoryByConversationId(Integer conversationId);
}
