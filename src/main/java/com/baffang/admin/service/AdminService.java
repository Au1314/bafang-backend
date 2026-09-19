package com.baffang.admin.service;


import java.util.List;

import com.baffang.admin.dto.AdminInfoDTO;
import com.baffang.admin.dto.AdminTypeUpdateDTO;

public interface AdminService {

    /**
     * 修改管理员身份
     * @param operatorId 操作者ID
     * @param updateDTO 更新信息
     * @return 更新后的管理员信息
     */
    AdminInfoDTO updateAdminType(Integer operatorId, AdminTypeUpdateDTO updateDTO);

    /**
     * 获取管理员列表（分页）
     */
    List<AdminInfoDTO> getAdminList(Integer operatorId, Integer page, Integer size);

    /**
     * 获取管理员详情
     */
    AdminInfoDTO getAdminDetail(Long adminId);
}
