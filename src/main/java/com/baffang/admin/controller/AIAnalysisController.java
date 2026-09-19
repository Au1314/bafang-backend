package com.baffang.admin.controller;

import com.baffang.admin.dto.AIAnalysisRequest;
import com.baffang.admin.dto.AIAnalysisResponse;
import com.baffang.admin.service.AIAnalysisService;
import com.baffang.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI 数据分析控制器
 */
@Slf4j
@Api(tags = "AI 数据分析")
@RestController
@RequestMapping("/api/v1/admin/ai")
public class AIAnalysisController {

    @Autowired
    private AIAnalysisService aiAnalysisService;

    @ApiOperation("综合数据分析 - 分析所有模块数据")
    @GetMapping("/analysis/all")
    public Result<AIAnalysisResponse> analyzeAll() {
        log.info("请求综合数据分析");
        AIAnalysisResponse response = aiAnalysisService.analyzeAll();
        return response.isSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @ApiOperation("模块数据分析 - 分析指定模块数据")
    @PostMapping("/analysis/module")
    public Result<AIAnalysisResponse> analyzeModule(@RequestBody AIAnalysisRequest request) {
        log.info("请求模块分析: module={}, question={}", request.getModule(), request.getQuestion());
        AIAnalysisResponse response = aiAnalysisService.analyzeModule(request);
        return response.isSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @ApiOperation("自定义问题分析 - 基于数据回答任意问题")
    @PostMapping("/analysis/question")
    public Result<AIAnalysisResponse> analyzeQuestion(
            @ApiParam("分析问题") @RequestBody AIAnalysisRequest request) {
        log.info("请求问题分析: question={}", request.getQuestion());
        
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return Result.error("请输入问题");
        }
        
        AIAnalysisResponse response = aiAnalysisService.analyzeWithQuestion(request.getQuestion());
        return response.isSuccess() ? Result.success(response) : Result.error(response.getError());
    }

    @ApiOperation("快速分析 - 简单问题快速回答")
    @GetMapping("/analysis/quick")
    public Result<AIAnalysisResponse> quickAnalysis(
            @ApiParam("快速问题") @RequestParam String question) {
        log.info("快速分析: question={}", question);
        
        if (question == null || question.trim().isEmpty()) {
            return Result.error("请输入问题");
        }
        
        AIAnalysisResponse response = aiAnalysisService.analyzeWithQuestion(question);
        return response.isSuccess() ? Result.success(response) : Result.error(response.getError());
    }
}
