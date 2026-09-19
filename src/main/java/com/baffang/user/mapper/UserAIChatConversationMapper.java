package com.baffang.user.mapper;

import com.baffang.common.entity.AIChatConversation;
import com.baffang.user.vo.AIChatConversationListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI对话主表Mapper接口
 */
@Mapper
public interface UserAIChatConversationMapper {
    /**
     * 插入AI对话会话
     *
     * @param conversation AI对话会话
     * @return 插入成功的行数
     */
    int insert(AIChatConversation conversation);

    /**
     * 根据会话ID查询AI对话会话
     *
     * @param sessionId 会话ID
     * @return AI对话会话
     */
    AIChatConversation selectBySessionId(String sessionId);

    /**
     * 根据ID更新AI对话会话
     *
     * @param conversation AI对话会话
     * @return 更新成功的行数
     */
    int updateById(AIChatConversation conversation);

    /**
     * 增加消息计数
     *
     * @param conversationId 对话ID
     * @return 更新成功的行数
     */
    int incrementMessageCount(@Param("conversationId") Integer conversationId);

    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AIChatConversationListVO> selectConversationListByUserId(@Param("userId") Integer userId);
}
