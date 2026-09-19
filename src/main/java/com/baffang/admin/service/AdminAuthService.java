package com.baffang.admin.service;

import com.baffang.admin.dto.AdminRegisterDTO;
import com.baffang.admin.dto.InviteCodeGenerateDTO;
import com.baffang.admin.vo.AdminRegisterVO;
import com.baffang.admin.vo.InviteCodeVO;

import java.util.List;

public interface AdminAuthService {
    
    /**
     * 管理员注册
     */
    AdminRegisterVO register(AdminRegisterDTO registerDTO);
    
    /**
     * 生成邀请码（仅超级管理员）
     */
    InviteCodeVO generateInviteCode(Integer adminId, InviteCodeGenerateDTO generateDTO);
    
    /**
     * 查询我创建的邀请码
     */
    List<InviteCodeVO> getMyInviteCodes(Integer adminId);
}
