package com.baffang.admin.mapper;

import com.baffang.common.entity.ConversationParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ConversationParticipantMapper {
    
    ConversationParticipant selectById(@Param("id") Integer id);
    
    int insert(ConversationParticipant participant);
    
    int updateById(ConversationParticipant participant);
    
    int deleteById(@Param("id") Integer id);
    
    ConversationParticipant selectAdminParticipant(@Param("conversationId") Integer conversationId);
    
    ConversationParticipant selectByConversationAndParticipant(
            @Param("conversationId") Integer conversationId,
            @Param("participantId") Integer participantId,
            @Param("participantType") Integer participantType);
    
    int incrementUnreadCount(@Param("conversationId") Integer conversationId,
                             @Param("participantType") Integer participantType);
    
    int updateReadStatus(@Param("conversationId") Integer conversationId,
                         @Param("participantId") Integer participantId,
                         @Param("participantType") Integer participantType,
                         @Param("lastReadTime") Date lastReadTime);
    
    List<Map<String, Object>> selectParticipantsByConversationId(@Param("conversationId") Integer conversationId);
    
    List<ConversationParticipant> selectByConversationId(@Param("conversationId") Integer conversationId);

    // ========== 新增方法 ==========

    /**
     * 查询对话中的管理员参与者
     */
    @Select("SELECT * FROM conversation_participant WHERE conversation_id = #{conversationId} AND participant_type = 1 AND is_deleted = 0")
    List<ConversationParticipant> selectAdminsByConversationId(@Param("conversationId") Integer conversationId);

    /**
     * 增加未读消息数（除发送者外）
     */
    @Update("UPDATE conversation_participant SET unread_count = unread_count + 1 WHERE conversation_id = #{conversationId} AND participant_id != #{senderId} AND is_deleted = 0")
    void incrementUnreadCountExceptSender(@Param("conversationId") Integer conversationId, @Param("senderId") Integer senderId);

    /**
     * 清零未读消息数
     */
    @Update("UPDATE conversation_participant SET unread_count = 0, last_read_time = NOW() WHERE conversation_id = #{conversationId} AND participant_id = #{participantId} AND is_deleted = 0")
    void clearUnreadCount(@Param("conversationId") Integer conversationId, @Param("participantId") Integer participantId);
}
