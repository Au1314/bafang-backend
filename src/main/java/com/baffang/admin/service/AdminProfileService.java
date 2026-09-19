package com.baffang.admin.service;

import com.baffang.admin.dto.AdminPasswordUpdateDTO;
import com.baffang.admin.dto.AdminProfileUpdateDTO;
import com.baffang.admin.vo.AdminProfileVO;
import org.springframework.web.multipart.MultipartFile;

public interface AdminProfileService {
    
    /**
     * 获取个人信息
     */
    AdminProfileVO getProfile(Integer adminId);
    
    /**
     * 修改个人信息
     */
    void updateProfile(Integer adminId, AdminProfileUpdateDTO dto);
    
    /**
     * 修改密码
     */
    void updatePassword(Integer adminId, AdminPasswordUpdateDTO dto);
    
    /**
     * 上传头像
     */
    String uploadAvatar(Integer adminId, MultipartFile file);
}
