package com.baffang.user.mapper;

import com.baffang.common.entity.CarAuth;
import com.baffang.user.dto.CarAuthDTO;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户端车辆认证Mapper
 */
@Mapper
public interface UserCarAuthMapper {
    /**
     * 新增车辆认证
     * 
     * @param carAuth 车辆认证信息
     * @return 影响行数
     */
    int insertCarAuth(CarAuth carAuth);

    /**
     * 查询用户是否有未删除的车辆认证记录
     * 
     * @param userId 用户ID
     * @return 车辆认证数量
     */
    int countCarAuthByUserId(Integer userId);

    /**
     * 根据用户ID查询车辆认证信息
     * 
     * @param userId 用户ID
     * @return 车辆认证信息
     */
    CarAuth getCarAuthByUserId(Integer userId);

    /**
     * 删除车辆认证信息（软删除）
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteCarAuthByUserId(Integer userId);

    /**
     * 更新车辆认证信息
     *
     * @param params 包含userId和carAuthDTO的参数Map
     * @return 影响行数
     */
    int updateCarAuthByUserId(Map<String, Object> params);
}