package com.baffang.admin.mapper;

import com.baffang.admin.dto.ForumReportQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ForumReportMapper {

    /**
     * 查询帖子举报列表（audit_type = 5）
     */
    List<Map<String, Object>> selectPostReportList(@Param("query") ForumReportQueryDTO query);

    /**
     * 查询评论举报列表（audit_type = 4）
     */
    List<Map<String, Object>> selectReplyReportList(@Param("query") ForumReportQueryDTO query);

    /**
     * 查询举报详情
     */
    Map<String, Object> selectDetailById(@Param("id") Integer id);

    /**
     * 检查举报记录是否存在
     */
    boolean existsById(@Param("id") Integer id);

    /**
     * 获取当前状态
     */
    Integer selectCurrentStatus(@Param("id") Integer id);

    /**
     * 审核通过
     */
    int updateStatusApproved(@Param("id") Integer id,
                              @Param("adminId") Integer adminId,
                              @Param("opinion") String opinion,
                              @Param("finishTime") LocalDateTime finishTime);

    /**
     * 审核拒绝
     */
    int updateStatusRejected(@Param("id") Integer id,
                              @Param("adminId") Integer adminId,
                              @Param("opinion") String opinion,
                              @Param("rejectReason") String rejectReason,
                              @Param("finishTime") LocalDateTime finishTime);

    /**
     * 查询举报统计
     */
    Map<String, Object> selectStatistics();

    /**
     * 根据ID查询业务ID和审核类型
     */
    Map<String, Object> selectBusinessInfo(@Param("id") Integer id);
}
