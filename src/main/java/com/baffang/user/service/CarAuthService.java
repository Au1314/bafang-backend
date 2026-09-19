package com.baffang.user.service;

import com.baffang.common.entity.CarAuth;
import com.baffang.user.dto.CarAuthDTO;

/**
 * 车辆认证服务接口
 */
public interface CarAuthService {
    /**
     * 新增车辆认证
     * 
     * @param userId     用户ID
     * @param carAuthDTO 车辆认证请求参数
     */
    void addCarAuth(Integer userId, CarAuthDTO carAuthDTO);

    /**
     * 根据用户ID查询车辆认证信息
     * 
     * @param userId 用户ID
     * @return 车辆认证信息
     */
    CarAuth getCarAuthByUserId(Integer userId);

    /**
     * 删除车辆认证信息
     *
     * @param userId 用户ID
     */
    void deleteCarAuth(Integer userId);

    /**
     * 更新车辆认证信息
     *
     * @param userId     用户ID
     * @param carAuthDTO 车辆认证更新参数
     */
    void updateCarAuth(Integer userId, CarAuthDTO carAuthDTO);
}