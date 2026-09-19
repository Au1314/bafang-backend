package com.baffang.user.service;

import com.baffang.common.entity.PublicChargeStation;

/**
 * 公桩服务接口
 */
public interface PublicStationService {

    /**
     * 根据公桩ID查询公桩详细信息
     * 
     * @param id 公桩ID
     * @return 公桩详细信息
     */
    PublicChargeStation getPublicStationById(Integer id);
}