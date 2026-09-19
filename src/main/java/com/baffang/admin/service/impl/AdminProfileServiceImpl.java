package com.baffang.admin.service.impl;

import com.baffang.admin.dto.AdminPasswordUpdateDTO;
import com.baffang.admin.dto.AdminProfileUpdateDTO;
import com.baffang.admin.mapper.AdminProfileMapper;
import com.baffang.admin.service.AdminProfileService;
import com.baffang.admin.vo.AdminProfileVO;
import com.baffang.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProfileServiceImpl implements AdminProfileService {

    private final AdminProfileMapper adminProfileMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 文件上传路径
     */
    @Value("${file.upload.path:${user.dir}/files/common}")
    private String uploadPath;

    /**
     * 文件访问URL前缀
     */
    @Value("${file.upload.url-prefix:http://localhost:8080/files}")
    private String urlPrefix;

    /**
     * 允许的图片类型
     */
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    /**
     * 最大文件大小（2MB）
     */
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    // ==================== 获取个人信息 ====================

    @Override
    public AdminProfileVO getProfile(Integer adminId) {
        Map<String, Object> map = adminProfileMapper.selectById(adminId);
        if (map == null || map.isEmpty()) {
            throw new BusinessException("管理员不存在");
        }
        return convertToVO(map);
    }

    // ==================== 修改个人信息 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Integer adminId, AdminProfileUpdateDTO dto) {
        // 至少需要修改一项
        if (!StringUtils.hasText(dto.getName()) && !StringUtils.hasText(dto.getAvatar())) {
            throw new BusinessException("请至少修改一项信息");
        }

        int updated = adminProfileMapper.updateProfile(adminId, dto.getName(), dto.getAvatar());
        if (updated == 0) {
            throw new BusinessException("修改失败");
        }

        log.info("管理员[{}]修改个人信息", adminId);
    }

    // ==================== 修改密码 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Integer adminId, AdminPasswordUpdateDTO dto) {
        // 1. 校验新密码与确认密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 校验新密码不能与旧密码相同
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        // 3. 校验原密码是否正确
        String currentPassword = adminProfileMapper.selectPasswordById(adminId);
        if (currentPassword == null) {
            throw new BusinessException("管理员不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), currentPassword)) {
            throw new BusinessException("原密码错误");
        }

        // 4. 加密新密码并更新
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        int updated = adminProfileMapper.updatePassword(adminId, encodedPassword);
        if (updated == 0) {
            throw new BusinessException("修改密码失败");
        }

        log.info("管理员[{}]修改密码成功", adminId);
    }

    // ==================== 上传头像 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(Integer adminId, MultipartFile file) {
        // 1. 校验文件
        validateImageFile(file);

        // 2. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = "avatar_" + adminId + "_" + UUID.randomUUID().toString().replace("-", "") + extension;

        // 3. 确保目录存在（使用 admin/avatar 子目录）
        String avatarDir = uploadPath + "/admin/avatar";
        File dir = new File(avatarDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new BusinessException("创建上传目录失败");
            }
        }

        // 4. 保存文件
        File destFile = new File(avatarDir, newFilename);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            log.error("上传头像失败", e);
            throw new BusinessException("上传头像失败");
        }

        // 5. 生成访问URL
        String avatarUrl = urlPrefix + "/admin/avatar/" + newFilename;

        // 6. 更新数据库
        int updated = adminProfileMapper.updateAvatar(adminId, avatarUrl);
        if (updated == 0) {
            // 删除已上传的文件
            destFile.delete();
            throw new BusinessException("更新头像失败");
        }

        log.info("管理员[{}]上传头像成功: {}", adminId, avatarUrl);
        return avatarUrl;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("图片大小不能超过2MB");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        boolean isAllowed = false;
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw new BusinessException("只支持 JPG、PNG、GIF、WEBP 格式的图片");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return ".jpg";
        }
        int lastDot = filename.lastIndexOf(".");
        if (lastDot == -1) {
            return ".jpg";
        }
        return filename.substring(lastDot).toLowerCase();
    }

    /**
     * 转换为VO
     */
    private AdminProfileVO convertToVO(Map<String, Object> map) {
        AdminProfileVO vo = new AdminProfileVO();
        vo.setId(getInteger(map, "id"));
        vo.setUsername(getString(map, "username"));
        vo.setName(getString(map, "name"));
        vo.setAvatar(getString(map, "avatar"));
        vo.setType(getInteger(map, "type"));
        vo.setTypeName(convertTypeName(vo.getType()));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setLastLoginTime(getLocalDateTime(map, "lastLoginTime"));
        return vo;
    }

    /**
     * 转换管理员类型名称
     */
    private String convertTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 0: return "超级管理员";
            case 1: return "普通管理员";
            case 2: return "审核员";
            case 3: return "客服";
            case 4: return "紧急响应人员";
            case 5: return "论坛管理员";
            case 10: return "未分配身份";
            default: return "未知";
        }
    }

    // ==================== 工具方法 ====================

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime getLocalDateTime(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        return null;
    }
}
