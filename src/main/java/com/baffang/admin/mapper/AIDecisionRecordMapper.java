package com.baffang.admin.mapper;

import com.baffang.admin.dto.AIDecisionQueryDTO;
import com.baffang.admin.vo.AIDecisionListVO;
import com.baffang.common.entity.AIDecisionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface AIDecisionRecordMapper {
    
    // ===== 基础 CRUD =====
    
    /**
     * 根据ID查询
     */
    AIDecisionRecord selectById(@Param("id") Integer id);
    
    /**
     * 插入记录
     */
    int insert(AIDecisionRecord record);
    
    /**
     * 更新记录
     */
    int updateById(AIDecisionRecord record);
    
    /**
     * 删除记录（逻辑删除）
     */
    int deleteById(@Param("id") Integer id);
    
    // ===== 业务查询 =====
    
    /**
     * 分页查询决策列表
     */
    List<AIDecisionListVO> selectPageList(@Param("query") AIDecisionQueryDTO query,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);
    
    /**
     * 查询总数
     */
    Long countByCondition(@Param("query") AIDecisionQueryDTO query);
    
    /**
     * 获取待处理的决策记录
     */
    List<AIDecisionRecord> selectPendingDecisions();
    
    /**
     * 获取待自动执行的决策
     */
    List<AIDecisionRecord> selectPendingAutoExecute(@Param("threshold") BigDecimal threshold);
    
    // ===== 统计 =====
    
    /**
     * 按类型统计
     */
    List<Map<String, Object>> countByType();
    
    /**
     * 按状态统计
     */
    List<Map<String, Object>> countByStatus();
    
    /**
     * 按优先级统计
     */
    List<Map<String, Object>> countByPriority();
    
    /**
     * 今日决策数
     */
    Integer countToday();
    
    /**
     * 平均置信度
     */
    Double avgConfidence();
}
