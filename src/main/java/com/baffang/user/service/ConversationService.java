package com.baffang.user.service;

import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.user.dto.SendMessageDTO;
import com.baffang.user.vo.ConversationCreateVO;
import com.baffang.user.vo.ConversationListVO;

/**
 * 对话服务接口
 */
public interface ConversationService {

    /**
     * 创建与私桩桩主预约对话
     *
     * @param userId    用户ID
     * @param stationId 私桩ID
     * @return 对话创建结果VO
     */
    ConversationCreateVO createConversationWithStationOwner(Integer userId, Integer stationId);

    /**
     * 查询用户所有对话消息
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 对话列表分页结果
     */
    Result<PageResult<ConversationListVO>> queryConversationList(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 发送消息
     *
     * @param userId         用户ID
     * @param conversationId 对话ID
     * @param sendMessageDTO 发送消息请求参数
     * @return 消息发送结果
     */
    Result<?> sendMessage(Integer userId, Integer conversationId, SendMessageDTO sendMessageDTO);

    /**
     * 查询对话消息列表
     *
     * @param userId         用户ID
     * @param conversationId 对话ID
     * @param pageNum        页码
     * @param pageSize       每页条数
     * @return 消息列表分页结果
     */
    Result<PageResult<?>> queryConversationMessages(Integer userId, Integer conversationId, Integer pageNum,
            Integer pageSize);

    /**
     * 结束对话
     *
     * @param userId         用户ID
     * @param conversationId 对话ID
     * @return 结束对话结果
     */
    Result<?> endConversation(Integer userId, Integer conversationId);

    /**
     * 发起人工客服咨询
     *
     * @param userId  用户ID
     * @param content 咨询内容
     * @return 发起咨询结果
     */
    Result<?> initiateManualServiceConsultation(Integer userId, String content);
}