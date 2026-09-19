package com.baffang.user.service.impl;

import com.baffang.common.entity.PublicChargeStation;
import com.baffang.user.mapper.UserPublicChargeStationMapper;
import com.baffang.user.service.PublicStationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 公桩服务实现类
 */
@Service
public class PublicStationServiceImpl implements PublicStationService {

    @Resource
    private UserPublicChargeStationMapper userPublicChargeStationMapper;

    @Override
    public PublicChargeStation getPublicStationById(Integer id) {
        if (id == null) {
            return null;
        }
        return userPublicChargeStationMapper.selectPublicStationById(id);
    }
}