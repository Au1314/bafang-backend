package com.baffang.admin.service;

import com.baffang.admin.dto.AIAnalysisRequest;
import com.baffang.admin.dto.AIAnalysisResponse;

/**
 * AI 数据分析服务
 */
public interface AIAnalysisService {

    /**
     * 综合数据分析
     */
    AIAnalysisResponse analyzeAll();

    /**
     * 指定模块分析
     */
    AIAnalysisResponse analyzeModule(AIAnalysisRequest request);

    /**
     * 自定义问题分析
     */
    AIAnalysisResponse analyzeWithQuestion(String question);
}
