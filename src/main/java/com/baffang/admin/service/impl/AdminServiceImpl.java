package com.baffang.admin.service.impl;

import com.baffang.admin.mapper.AdminMapper;
import com.baffang.admin.service.AdminService;
import com.baffang.common.constant.AdminTypeConstants;
import com.baffang.admin.dto.AdminInfoDTO;
import com.baffang.admin.dto.AdminTypeUpdateDTO;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminInfoDTO updateAdminType(Integer operatorId, AdminTypeUpdateDTO updateDTO) {
        // 1. 获取操作者信息
        AdminInfo operator = adminMapper.selectById(Long.valueOf(operatorId));
        if (operator == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 2. 检查操作者是否有权限修改他人身份
        if (!AdminTypeConstants.canModifyOthers(operator.getType())) {
            throw new BusinessException(ErrorMsg.NOT_SUPER_ADMIN);
        }

        // 3. 获取目标管理员信息
        AdminInfo targetAdmin = adminMapper.selectById(updateDTO.getTargetAdminId());
        if (targetAdmin == null || targetAdmin.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.TARGET_ADMIN_NOT_FOUND);
        }

        // 4. 不能修改自己的身份
        if (operatorId.equals(targetAdmin.getId())) {
            throw new BusinessException(ErrorMsg.CANNOT_MODIFY_SELF);
        }

        // 5. 不能修改超级管理员的身份
        if (targetAdmin.getType() == AdminTypeConstants.SUPER_ADMIN) {
            throw new BusinessException(ErrorMsg.CANNOT_MODIFY_SUPER_ADMIN);
        }

        // 6. 检查操作者是否有权限设置目标身份
        Integer newType = updateDTO.getNewType();
        if (!AdminTypeConstants.canSetType(operator.getType(), newType)) {
            throw new BusinessException(ErrorMsg.NO_PERMISSION_TO_SET_TYPE);
        }

        // 7. 普通管理员不能修改其他普通管理员和论坛管理员的身份
        if (operator.getType() == AdminTypeConstants.NORMAL_ADMIN) {
            if (targetAdmin.getType() == AdminTypeConstants.NORMAL_ADMIN ||
                targetAdmin.getType() == AdminTypeConstants.FORUM_ADMIN) {
                throw new BusinessException(ErrorMsg.NO_PERMISSION_TO_SET_TYPE);
            }
        }

        // 8. 执行更新 - 使用你现有的 updateById 方法
        String oldTypeName = AdminTypeConstants.getTypeName(targetAdmin.getType());
        targetAdmin.setType(newType);
        targetAdmin.setUpdateTime(new Date());
        adminMapper.updateById(targetAdmin);

        log.info("管理员身份变更: 操作者={}, 目标={}, 原身份={}, 新身份={}",
                operator.getUsername(), targetAdmin.getUsername(),
                oldTypeName, AdminTypeConstants.getTypeName(newType));

        // 9. 返回更新后的信息
        AdminInfo updatedAdmin = adminMapper.selectById(updateDTO.getTargetAdminId());
        return convertToDTO(updatedAdmin);
    }

    @Override
    public List<AdminInfoDTO> getAdminList(Integer operatorId, Integer page, Integer size) {
        // 获取操作者信息
        AdminInfo operator = adminMapper.selectById(Long.valueOf(operatorId));
        if (operator == null) {
            throw new BusinessException(ErrorMsg.ACCOUNT_NOT_FOUND);
        }

        // 只有超管和普通管理员可以查看列表
        if (!AdminTypeConstants.canModifyOthers(operator.getType())) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 分页查询 - 使用你现有的 selectList 方法
        int offset = (page - 1) * size;
        List<AdminInfo> adminList = adminMapper.selectList(offset, size);

        return adminList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminInfoDTO getAdminDetail(Long adminId) {
        AdminInfo admin = adminMapper.selectById(adminId);
        if (admin == null || admin.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.TARGET_ADMIN_NOT_FOUND);
        }
        return convertToDTO(admin);
    }

    /**
     * 转换为DTO
     */
    private AdminInfoDTO convertToDTO(AdminInfo admin) {
        AdminInfoDTO dto = new AdminInfoDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setName(admin.getName());
        dto.setAvatar(admin.getAvatar());
        dto.setType(admin.getType());
        dto.setTypeName(AdminTypeConstants.getTypeName(admin.getType()));
        dto.setCreateTime(admin.getCreateTime());
        dto.setUpdateTime(admin.getUpdateTime());
        return dto;
    }
}
