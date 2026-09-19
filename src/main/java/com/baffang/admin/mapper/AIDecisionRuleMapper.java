package com.baffang.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baffang.common.entity.AIDecisionRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AIDecisionRuleMapper extends BaseMapper<AIDecisionRule> {
    
    /**
     * 获取启用的规则列表
     */
    List<AIDecisionRule> selectEnabledRules();
    
    /**
     * 根据规则编码查询
     */
    AIDecisionRule selectByRuleCode(@Param("ruleCode") String ruleCode);
}
