// ==================== 客服服务接口 ====================
package com.baffang.admin.service;

import java.util.List;

import com.baffang.admin.dto.*;
import com.baffang.admin.vo.*;
import com.github.pagehelper.PageInfo;

public interface CustomerServiceService {
    
    // ========== AI配置管理 ==========
    
    /**
     * 获取AI配置
     */
    AiConfigVO getAiConfig();
    
    /**
     * 更新AI配置
     */
    void updateAiConfig(AiConfigDTO dto);
    
    // ========== 人工客服对话 ==========
    
    /**
     * 获取待接入对话列表
     */
    PageInfo<PendingConversationVO> getPendingConversations(Integer pageNum, Integer pageSize);
    
    /**
     * 客服接入对话
     */
    void joinConversation(Integer conversationId, Integer adminId);
    
    /**
     * 发送消息
     */
    MessageVO sendMessage(Integer conversationId, SendMessageDTO dto, Integer adminId);
    
    /**
     * 获取对话消息列表
     */
    PageInfo<MessageVO> getMessages(Integer conversationId, Integer pageNum, Integer pageSize, Integer adminId);
    
    /**
     * 结束对话
     */
    void closeConversation(Integer conversationId, Integer adminId);
    
    /**
     * 转接对话
     */
    void transferConversation(Integer conversationId, TransferConversationDTO dto, Integer adminId);
    
    // ========== 对话记录管理 ==========
    
    /**
     * 分页查询对话列表
     */
    PageInfo<ConversationListVO> getConversationList(ConversationQueryDTO queryDTO);
    
    /**
     * 获取对话详情
     */
    ConversationDetailVO getConversationDetail(Integer conversationId);

    /**
 * 获取我的对话列表
 */
PageInfo<ConversationListVO> getMyConversations(Integer pageNum, Integer pageSize, Integer status, Integer adminId);

// CustomerServiceService.java 添加以下方法

/**
 * 获取客服统计概览
 */
CustomerServiceStatsVO getStatsOverview(Integer adminId);

/**
 * 获取我的统计数据
 */
AdminStatsVO getMyStats(Integer adminId);

/**
 * 获取所有客服统计
 */
List<AdminStatsVO> getAllAdminStats();


}
