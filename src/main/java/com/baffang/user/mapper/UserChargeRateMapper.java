package com.baffang.user.mapper;

import com.baffang.common.entity.ChargeRate;
import com.baffang.user.vo.ChargeRateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 充电桩评论Mapper接口（用户端）
 */
@Mapper
public interface UserChargeRateMapper {

    /**
     * 插入评论
     * 
     * @param chargeRate 评论信息
     * @return 影响的行数
     */
    int insertChargeRate(ChargeRate chargeRate);

    /**
     * 计算充电桩的平均评分
     * 
     * @param stationId   充电桩ID
     * @param stationType 桩类型（0：公桩，1：私桩）
     * @return 平均评分
     */
    Double calculateAverageRating(@Param("stationId") Integer stationId, @Param("stationType") Integer stationType);

    /**
     * 根据充电桩ID和类型查询评论列表
     * 
     * @param stationId   充电桩ID
     * @param stationType 桩类型（0：公桩，1：私桩）
     * @return 评论列表
     */
    List<ChargeRateVO> selectChargeRateListByStationIdAndType(@Param("stationId") Integer stationId,
            @Param("stationType") Integer stationType);

    /**
     * 根据评论ID查询评论信息
     * 
     * @param rateId 评论ID
     * @return 评论信息
     */
    ChargeRate selectChargeRateById(@Param("rateId") Integer rateId);

    /**
     * 删除评论（软删除，将is_deleted设为1）
     * 
     * @param rateId 评论ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteChargeRate(@Param("rateId") Integer rateId, @Param("userId") Integer userId);
}