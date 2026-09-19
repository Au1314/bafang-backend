package com.baffang.admin.mapper;

import com.baffang.admin.dto.ConversationQueryDTO;
import com.baffang.admin.vo.ConversationListVO;
import com.baffang.common.entity.Conversation;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ConversationMapper {
    
    Conversation selectById(@Param("id") Integer id);
    
    int insert(Conversation conversation);
    
    int updateById(Conversation conversation);
    
    int deleteById(@Param("id") Integer id);
    
    Page<Map<String, Object>> selectConversationList(@Param("query") ConversationQueryDTO query);
    
    Page<Map<String, Object>> selectPendingConversations();
    
    Map<String, Object> selectConversationDetail(@Param("id") Integer id);
    
    List<ConversationListVO> selectMyConversations(@Param("adminId") Integer adminId, @Param("status") Integer status);

    /**
     * 按状态统计数量
     */
    @Select("SELECT COUNT(*) FROM conversation WHERE status = #{status} AND is_deleted = 0")
    Integer countByStatus(@Param("status") Integer status);

    /**
     * 统计客服当前进行中的对话数
     */
    @Select("""
        SELECT COUNT(DISTINCT c.id) 
        FROM conversation c
        INNER JOIN conversation_participant cp ON c.id = cp.conversation_id
        WHERE cp.participant_id = #{adminId} 
          AND cp.participant_type = 1
          AND cp.is_deleted = 0
          AND c.status = 1
          AND c.is_deleted = 0
        """)
    Integer countOngoingByAdminId(@Param("adminId") Integer adminId);

    /**
     * 统计客服今日接待数
     */
    @Select("""
        SELECT COUNT(DISTINCT c.id)
        FROM conversation c
        INNER JOIN conversation_participant cp ON c.id = cp.conversation_id
        WHERE cp.participant_id = #{adminId}
          AND cp.participant_type = 1
          AND cp.is_deleted = 0
          AND cp.join_time >= #{startTime}
          AND cp.join_time < #{endTime}
          AND c.is_deleted = 0
        """)
    Integer countTodayByAdminId(@Param("adminId") Integer adminId, 
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计客服今日已完成数
     */
    @Select("""
        SELECT COUNT(DISTINCT c.id)
        FROM conversation c
        INNER JOIN conversation_participant cp ON c.id = cp.conversation_id
        WHERE cp.participant_id = #{adminId}
          AND cp.participant_type = 1
          AND cp.is_deleted = 0
          AND c.status = 2
          AND c.update_time >= #{startTime}
          AND c.update_time < #{endTime}
          AND c.is_deleted = 0
        """)
    Integer countTodayCompletedByAdminId(@Param("adminId") Integer adminId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计客服指定时间范围内接待数
     */
    @Select("""
        SELECT COUNT(DISTINCT c.id)
        FROM conversation c
        INNER JOIN conversation_participant cp ON c.id = cp.conversation_id
        WHERE cp.participant_id = #{adminId}
          AND cp.participant_type = 1
          AND cp.is_deleted = 0
          AND cp.join_time >= #{startTime}
          AND cp.join_time < #{endTime}
          AND c.is_deleted = 0
        """)
    Integer countByAdminIdAndTimeRange(@Param("adminId") Integer adminId,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);
}
