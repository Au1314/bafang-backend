package com.baffang.admin.controller;

import com.baffang.admin.dto.AdminPasswordUpdateDTO;
import com.baffang.admin.dto.AdminProfileUpdateDTO;
import com.baffang.admin.service.AdminProfileService;
import com.baffang.admin.vo.AdminProfileVO;
import com.baffang.common.constant.AuthConstants;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Tag(name = "管理员个人信息管理")
@RestController
@RequestMapping("/api/v1/admin/profile")
@RequiredArgsConstructor
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @Operation(summary = "获取个人信息")
    @GetMapping
    public Result<AdminProfileVO> getProfile(HttpServletRequest request) {
        Integer adminId = getAdminIdFromRequest(request);
        return Result.success(adminProfileService.getProfile(adminId));
    }

    @Operation(summary = "修改个人信息")
    @PutMapping
    public Result<Void> updateProfile(@RequestBody @Validated AdminProfileUpdateDTO dto,
                                      HttpServletRequest request) {
        Integer adminId = getAdminIdFromRequest(request);
        adminProfileService.updateProfile(adminId, dto);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody @Validated AdminPasswordUpdateDTO dto,
                                       HttpServletRequest request) {
        Integer adminId = getAdminIdFromRequest(request);
        adminProfileService.updatePassword(adminId, dto);
        return Result.success();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                       HttpServletRequest request) {
        Integer adminId = getAdminIdFromRequest(request);
        String avatarUrl = adminProfileService.uploadAvatar(adminId, file);
        return Result.success(avatarUrl);
    }

    // ==================== 私有方法 ====================

    /**
     * 从请求头中获取管理员ID
     */
    private Integer getAdminIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(JwtConstants.TOKEN_PREFIX)) {
            throw new BusinessException(ErrorMsg.USER_NOT_LOGIN);
        }

        String token = authHeader.substring(JwtConstants.TOKEN_PREFIX.length()).trim();

        try {
            Claims claims = JwtUtil.parseToken(JwtConstants.SECRET_KEY, token);

            // 优先获取 adminId（新格式）
            Object adminIdObj = claims.get("adminId");
            if (adminIdObj != null) {
                if (adminIdObj instanceof Number) {
                    return ((Number) adminIdObj).intValue();
                }
            }

            // 兼容旧格式 userId
            Object userIdObj = claims.get(JwtConstants.USER_ID_CLAIM);
            if (userIdObj != null) {
                if (userIdObj instanceof Number) {
                    return ((Number) userIdObj).intValue();
                }
            }

            log.warn("Token中未找到有效的用户ID, claims: {}", claims);
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token解析异常: {}", e.getMessage());
            throw new BusinessException(ErrorMsg.TOKEN_INVALID);
        }
    }
}
