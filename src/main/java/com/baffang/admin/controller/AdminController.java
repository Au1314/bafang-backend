package com.baffang.admin.controller;

import com.baffang.admin.dto.AdminInfoDTO;
import com.baffang.admin.dto.AdminTypeUpdateDTO;
import com.baffang.admin.mapper.AdminMapper;
import com.baffang.common.constant.AuthConstants;
import com.baffang.common.constant.JwtConstants;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.enums.AdminRoleEnum;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/member")
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 超级管理员可设置的身份类型: 1,2,3,4,5,10
     */
    private static final Set<Integer> SUPER_ADMIN_CAN_SET = new HashSet<>(
            Arrays.asList(1, 2, 3, 4, 5, 10)
    );

    /**
     * 普通管理员可设置的身份类型: 2,3,4,10
     */
    private static final Set<Integer> NORMAL_ADMIN_CAN_SET = new HashSet<>(
            Arrays.asList(2, 3, 4, 10)
    );

    /**
     * 修改管理员身份
     */
    @PutMapping("/type")
    public Result<AdminInfoDTO> updateAdminType(
            @RequestBody @Validated AdminTypeUpdateDTO updateDTO,
            HttpServletRequest request) {

        // 1. 获取操作者信息
        Integer operatorId = getAdminIdFromRequest(request);
        AdminInfo operator = adminMapper.selectById(Long.valueOf(operatorId));
        if (operator == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 检查操作者是否有权限修改他人身份（只有超管和普通管理员）
        if (operator.getType() != 0 && operator.getType() != 1) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 3. 获取目标管理员信息
        AdminInfo targetAdmin = adminMapper.selectById(updateDTO.getTargetAdminId());
        if (targetAdmin == null) {
            throw new BusinessException(ErrorMsg.TARGET_ADMIN_NOT_FOUND);
        }

        // 4. 不能修改自己的身份
        if (operatorId.equals(targetAdmin.getId())) {
            throw new BusinessException(ErrorMsg.CANNOT_MODIFY_SELF);
        }

        // 5. 不能修改超级管理员的身份
        if (targetAdmin.getType() == 0) {
            throw new BusinessException(ErrorMsg.CANNOT_MODIFY_SUPER_ADMIN);
        }

        // 6. 普通管理员不能修改普通管理员和论坛管理员的身份
        if (operator.getType() == 1) {
            if (targetAdmin.getType() == 1 || targetAdmin.getType() == 5) {
                throw new BusinessException(ErrorMsg.NO_PERMISSION_TO_MODIFY_TARGET);
            }
        }

        // 7. 检查操作者是否有权限设置目标身份
        Integer newType = updateDTO.getNewType();
        boolean canSet = false;
        if (operator.getType() == 0) {
            canSet = SUPER_ADMIN_CAN_SET.contains(newType);
        } else if (operator.getType() == 1) {
            canSet = NORMAL_ADMIN_CAN_SET.contains(newType);
        }
        if (!canSet) {
            throw new BusinessException(ErrorMsg.NO_PERMISSION_TO_SET_TYPE);
        }

        // 8. 执行更新
        String oldTypeName = getTypeName(targetAdmin.getType());
        targetAdmin.setType(newType);
        targetAdmin.setUpdateTime(new Date());
        adminMapper.updateById(targetAdmin);

        log.info("管理员身份变更: 操作者={}, 目标={}, 原身份={}, 新身份={}",
                operator.getUsername(), targetAdmin.getUsername(),
                oldTypeName, getTypeName(newType));

        // 9. 返回更新后的信息
        return Result.success(convertToDTO(targetAdmin));
    }

    /**
     * 获取管理员列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getAdminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {

        // 获取操作者信息
        Integer operatorId = getAdminIdFromRequest(request);
        AdminInfo operator = adminMapper.selectById(Long.valueOf(operatorId));
        if (operator == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 只有超管和普通管理员可以查看列表
        if (operator.getType() != 0 && operator.getType() != 1) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 分页查询
        int offset = (page - 1) * size;
        List<AdminInfo> adminList = adminMapper.selectList(offset, size);
        int total = adminMapper.countAll();

        List<AdminInfoDTO> dtoList = adminList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", dtoList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return Result.success(result);
    }

    /**
     * 获取管理员详情
     */
    @GetMapping("/{adminId}")
    public Result<AdminInfoDTO> getAdminDetail(
            @PathVariable Long adminId,
            HttpServletRequest request) {

        // 验证登录
        getAdminIdFromRequest(request);

        AdminInfo admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ErrorMsg.TARGET_ADMIN_NOT_FOUND);
        }
        return Result.success(convertToDTO(admin));
    }

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
        
        // 使用 Number 类型兼容 Integer 和 Long
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


    /**
     * 转换为DTO
     */
    private AdminInfoDTO convertToDTO(AdminInfo admin) {
        AdminInfoDTO dto = new AdminInfoDTO();
        BeanUtils.copyProperties(admin, dto);
        dto.setTypeName(getTypeName(admin.getType()));
        return dto;
    }

    /**
     * 获取角色类型名称
     */
    private String getTypeName(Integer type) {
        if (type == null) return "未知";
        try {
            return AdminRoleEnum.fromCode(type).getDescription();
        } catch (Exception e) {
            return "未知";
        }
    }
}
