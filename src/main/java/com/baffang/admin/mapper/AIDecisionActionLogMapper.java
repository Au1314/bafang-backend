package com.baffang.admin.mapper;

import com.baffang.common.entity.AIDecisionActionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AIDecisionActionLogMapper {
    
    /**
     * 插入记录
     */
    int insert(AIDecisionActionLog log);
    
    /**
     * 更新记录
     */
    int updateById(AIDecisionActionLog log);
    
    /**
     * 根据ID查询
     */
    AIDecisionActionLog selectById(@Param("id") Integer id);
    
    /**
     * 根据决策ID查询执行日志
     */
    List<AIDecisionActionLog> selectByDecisionId(@Param("decisionId") Integer decisionId);
}
