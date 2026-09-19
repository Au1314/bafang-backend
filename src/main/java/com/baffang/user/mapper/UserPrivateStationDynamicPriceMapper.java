package com.baffang.user.mapper;

import com.baffang.common.entity.PrivateStationDynamicPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 私桩动态价格Mapper接口（用户端）
 */
@Mapper
public interface UserPrivateStationDynamicPriceMapper {

    /**
     * 根据私桩ID删除所有动态价格规则（逻辑删除）
     *
     * @param stationId 私桩ID
     * @return 影响的行数
     */
    int deleteDynamicPriceByStationId(@Param("stationId") Integer stationId);

    /**
     * 批量插入私桩动态价格规则
     *
     * @param dynamicPrices 动态价格规则列表
     * @return 影响的行数
     */
    int batchInsertDynamicPrice(@Param("dynamicPrices") List<PrivateStationDynamicPrice> dynamicPrices);

    /**
     * 根据私桩ID查询所有未删除的动态价格规则
     *
     * @param stationId 私桩ID
     * @return 动态价格规则列表
     */
    List<PrivateStationDynamicPrice> selectDynamicPriceByStationId(@Param("stationId") Integer stationId);

    /**
     * 删除私桩动态价格规则（逻辑删除）
     *
     * @param id        规则ID
     * @param stationId 私桩ID
     * @return 影响的行数
     */
    int deleteDynamicPriceRule(@Param("id") Integer id, @Param("stationId") Integer stationId);
}