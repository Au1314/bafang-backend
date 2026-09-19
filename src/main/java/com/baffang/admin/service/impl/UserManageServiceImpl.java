package com.baffang.admin.service.impl;

import com.baffang.admin.dto.*;
import com.baffang.admin.mapper.UserManageMapper;
import com.baffang.admin.service.UserManageService;
import com.baffang.admin.vo.*;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {

    private final UserManageMapper userManageMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
public Page<UserListVO> getUserList(UserQueryDTO query) {
    // ========== 分页参数处理 ==========
    Integer pageNum = query.getPageNum();
    Integer pageSize = query.getPageSize();
    
    int finalPageNum = pageNum == null ? 1 : pageNum;
    int finalPageSize = pageSize == null ? 10 : pageSize;

    PageHelper.startPage(finalPageNum, finalPageSize);
    
    // ========== 查询数据 ==========
    Page<UserListVO> userList = (Page<UserListVO>) userManageMapper.selectUserList(query);
    
    // ========== 关键：转换状态名称 ==========
    for (UserListVO vo : userList.getResult()) {
        convertStatusNames(vo);
    }
    
    return userList;
}

    @Override
    public UserDetailVO getUserDetail(Integer id) {
        Map<String, Object> userMap = userManageMapper.selectUserDetail(id);
        if (userMap == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserDetailVO vo = new UserDetailVO();
        vo.setId(getInteger(userMap, "id"));
        vo.setUsername(getString(userMap, "username"));
        vo.setName(getString(userMap, "name"));
        vo.setPhone(getString(userMap, "phone"));
        vo.setEmail(getString(userMap, "email"));
        vo.setAvatar(getString(userMap, "avatar"));
        vo.setSignature(getString(userMap, "signature"));
        vo.setType(getInteger(userMap, "type"));
        vo.setAuthChargingStation(getInteger(userMap, "authChargingStation"));
        vo.setAuthVehicle(getInteger(userMap, "authVehicle"));
        vo.setPrivacyVehicle(getInteger(userMap, "privacyVehicle"));
        vo.setPrivacyStation(getInteger(userMap, "privacyStation"));
        vo.setPoints(getInteger(userMap, "points"));
        vo.setBalance(getBigDecimal(userMap, "balance"));
        vo.setIsDeleted(getInteger(userMap, "isDeleted"));
        vo.setCreateTime(getLocalDateTime(userMap, "createTime"));
        vo.setUpdateTime(getLocalDateTime(userMap, "updateTime"));
        vo.setLastLoginTime(getLocalDateTime(userMap, "lastLoginTime"));
        
        // 转换状态名称
        convertDetailStatusNames(vo);
        
        // 查询扩展信息
        vo.setStationCount(userManageMapper.countUserStations(id));
        vo.setVehicleCount(userManageMapper.countUserVehicles(id));
        vo.setOrderCount(userManageMapper.countUserOrders(id));
        
        return vo;
    }

    @Override
    @Transactional
    public void updateUser(Integer id, UserUpdateDTO updateDTO) {
        // 检查用户是否存在
        if (!userManageMapper.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查手机号是否重复
        if (updateDTO.getPhone() != null) {
            Integer existId = userManageMapper.selectIdByPhone(updateDTO.getPhone());
            if (existId != null && !existId.equals(id)) {
                throw new BusinessException("手机号已被其他用户使用");
            }
        }
        
        // 更新用户信息
        userManageMapper.updateUser(id, updateDTO);
    }

    @Override
    @Transactional
    public void updateUserStatus(Integer id, UserStatusDTO statusDTO) {
        if (!userManageMapper.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        
        userManageMapper.updateUserDeleted(id, statusDTO.getStatus());
    }

    @Override
    @Transactional
    public String resetPassword(Integer id) {
        if (!userManageMapper.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        
        // 默认密码 123456
        String defaultPassword = "123456";
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        
        userManageMapper.updatePassword(id, encodedPassword);
        
        return defaultPassword;
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (!userManageMapper.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        
        userManageMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDeleteUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的用户");
        }
        
        userManageMapper.batchDelete(ids);
    }

    @Override
    public UserStatisticsVO getStatistics() {
        return userManageMapper.selectStatistics();
    }

    // ==================== 私有方法 ====================

    private void convertStatusNames(UserListVO vo) {
        vo.setTypeName(vo.getType() != null && vo.getType() == 1 ? "VIP用户" : "普通用户");
        vo.setAuthChargingStationName(convertAuthStatusName(vo.getAuthChargingStation()));
        vo.setAuthVehicleName(convertAuthStatusName(vo.getAuthVehicle()));
        vo.setStatusName(vo.getIsDeleted() != null && vo.getIsDeleted() == 1 ? "已禁用" : "正常");
    }

    private void convertDetailStatusNames(UserDetailVO vo) {
        vo.setTypeName(vo.getType() != null && vo.getType() == 1 ? "VIP用户" : "普通用户");
        vo.setAuthChargingStationName(convertAuthStatusName(vo.getAuthChargingStation()));
        vo.setAuthVehicleName(convertAuthStatusName(vo.getAuthVehicle()));
        vo.setPrivacyVehicleName(vo.getPrivacyVehicle() != null && vo.getPrivacyVehicle() == 1 ? "隐藏" : "公开");
        vo.setPrivacyStationName(vo.getPrivacyStation() != null && vo.getPrivacyStation() == 1 ? "隐藏" : "公开");
        vo.setStatusName(vo.getIsDeleted() != null && vo.getIsDeleted() == 1 ? "已禁用" : "正常");
    }

    private String convertAuthStatusName(Integer status) {
        if (status == null) return "未认证";
        switch (status) {
            case 0: return "未认证";
            case 1: return "审核中";
            case 2: return "已认证";
            case 3: return "认证失败";
            default: return "未知";
        }
    }

    // ==================== 类型转换辅助方法 ====================
    
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private java.math.BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof java.math.BigDecimal) return (java.math.BigDecimal) value;
        return new java.math.BigDecimal(value.toString());
    }

    private java.time.LocalDateTime getLocalDateTime(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof java.time.LocalDateTime) return (java.time.LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        return null;
    }
}
