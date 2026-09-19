package com.baffang.admin.service;

import com.baffang.admin.dto.StationAuthReviewDTO;
import com.baffang.admin.vo.StationAuthDetailVO;
import com.baffang.admin.vo.StationAuthListVO;
import com.github.pagehelper.Page;

public interface StationAuthService {

    Page<StationAuthListVO> getAuthList(Integer authStatus, Integer pageNum, Integer pageSize);

    StationAuthDetailVO getAuthDetail(Integer stationId);

    /**
     * 审核私桩认证
     * @param dto 审核参数
     * @param adminId 审核人ID
     */
    void reviewAuth(StationAuthReviewDTO dto, Integer adminId);
}
