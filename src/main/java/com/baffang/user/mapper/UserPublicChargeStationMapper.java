package com.baffang.user.mapper;

import com.baffang.common.entity.PublicChargeStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公桩表Mapper接口（用户端）
 */
@Mapper
public interface UserPublicChargeStationMapper {

    /**
     * 根据公桩ID查询公桩信息
     * 
     * @param id 公桩ID
     * @return 公桩信息
     */
    PublicChargeStation selectPublicStationById(@Param("id") Integer id);

    /**
     * 更新公桩评分
     * 
     * @param id     公桩ID
     * @param rating 评分
     * @return 影响的行数
     */
    int updatePublicStationRating(@Param("id") Integer id, @Param("rating") Double rating);

    /**
     * 插入单个公桩信息
     * 
     * @param publicChargeStation 公桩信息
     * @return 影响的行数
     */
    int insertPublicStation(PublicChargeStation publicChargeStation);

    /**
     * 批量插入公桩信息
     * 
     * @param publicChargeStations 公桩信息列表
     * @return 影响的行数
     */
    int batchInsertPublicStations(@Param("list") List<PublicChargeStation> publicChargeStations);
    
    /**
     * 根据经纬度范围查询公桩
     * 
     * @param minLng 最小经度
     * @param maxLng 最大经度
     * @param minLat 最小纬度
     * @param maxLat 最大纬度
     * @return 公桩列表
     */
    List<PublicChargeStation> selectPublicStationsByGeoRange(@Param("minLng") Double minLng, 
                                                             @Param("maxLng") Double maxLng, 
                                                             @Param("minLat") Double minLat, 
                                                             @Param("maxLat") Double maxLat);
}