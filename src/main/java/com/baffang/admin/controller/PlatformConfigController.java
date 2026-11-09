package com.baffang.admin.controller;

import com.baffang.admin.mapper.PlatformConfigMapper;
import com.baffang.common.annotation.SystemLog;
import com.baffang.common.entity.PlatformConfig;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台参数配置控制器（契约补齐：前端 PlatformManage 参数配置的持久化）
 * 单行配置（id=1），config_json 透传 JSON 对象，扩展字段无需改表
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/platform/config")
@Tag(name = "平台参数配置")
@RequiredArgsConstructor
public class PlatformConfigController {

    private final PlatformConfigMapper platformConfigMapper;
    private final ObjectMapper objectMapper;

    /**
     * 读取平台配置（返回 config_json 反序列化后的对象；无记录时返回 {}）
     */
    @GetMapping
    @Operation(summary = "读取平台配置")
    public Result<Object> getConfig(HttpServletRequest request) {
        checkAdminPermission(request, 1);
        PlatformConfig config = platformConfigMapper.selectOne();
        if (config == null || config.getConfigJson() == null) {
            return Result.success(objectMapper.createObjectNode());
        }
        try {
            return Result.success(objectMapper.readTree(config.getConfigJson()));
        } catch (Exception e) {
            log.error("平台配置 JSON 解析失败", e);
            throw new BusinessException("平台配置数据异常");
        }
    }

    /**
     * 保存平台配置（JSON 对象透传存储）
     */
    @PutMapping
    @Operation(summary = "保存平台配置")
    @SystemLog(module = "平台管理", action = "保存平台配置")
    public Result<Void> saveConfig(@RequestBody JsonNode body, HttpServletRequest request) {
        Integer adminId = checkAdminPermission(request, 1);
        if (body == null || body.isEmpty()) {
            throw new BusinessException("配置内容不能为空");
        }
        PlatformConfig config = new PlatformConfig();
        config.setId(1);
        config.setConfigJson(body.toString());
        config.setUpdateAdminId(adminId);
        platformConfigMapper.upsert(config);
        log.info("管理员[{}]更新平台配置", adminId);
        return Result.success();
    }

    /**
     * 校验管理员权限（普通管理员及以上）
     */
    private Integer checkAdminPermission(HttpServletRequest request, int maxType) {
        // 安全整改后统一由 AdminGuard 查库校验
        return adminGuard.requireAdmin(maxType);
    }

    private final com.baffang.framework.permission.AdminGuard adminGuard;
}
