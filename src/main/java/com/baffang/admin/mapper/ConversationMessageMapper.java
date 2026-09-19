package com.baffang.admin.mapper;

import com.baffang.common.entity.ConversationMessage;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ConversationMessageMapper {
    
    ConversationMessage selectById(@Param("id") Integer id);
    
    int insert(ConversationMessage message);
    
    int updateById(ConversationMessage message);
    
    int deleteById(@Param("id") Integer id);
    
    Page<Map<String, Object>> selectMessagesByConversationId(@Param("conversationId") Integer conversationId);
    
    /**
     * 标记对话消息已读（按发送者类型）- 原有方法保留
     */
    int markAsRead(@Param("conversationId") Integer conversationId,
                   @Param("senderType") Integer senderType);
    
    List<ConversationMessage> selectByConversationId(@Param("conversationId") Integer conversationId);

    /**
     * 统计客服今日发送消息数
     */
    @Select("""
        SELECT COUNT(*)
        FROM conversation_message
        WHERE sender_id = #{adminId}
          AND sender_type = 1
          AND send_time >= #{startTime}
          AND send_time < #{endTime}
          AND is_deleted = 0
        """)
    Integer countTodayByAdminId(@Param("adminId") Integer adminId,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    /**
     * 标记单条消息已读（按消息ID）
     */
    @Update("UPDATE conversation_message SET is_read = 1 WHERE id = #{messageId} AND sender_id != #{readerId}")
    void markMessageAsReadById(@Param("messageId") Integer messageId, @Param("readerId") Integer readerId);

    /**
     * 标记对话所有消息已读（按对话ID和阅读者）
     */
    @Update("UPDATE conversation_message SET is_read = 1 WHERE conversation_id = #{conversationId} AND sender_id != #{readerId} AND is_read = 0")
    void markAllMessagesAsReadByReader(@Param("conversationId") Integer conversationId, @Param("readerId") Integer readerId);
}
