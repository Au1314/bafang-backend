package com.baffang.user.mapper;

import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.user.dto.PrivateStationAuthDTO;
import com.baffang.user.vo.NaviPrivateStationVO;
import com.baffang.user.vo.PrivateStationListVO;
import com.baffang.user.vo.PrivateStationTimeConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 私桩表Mapper接口（用户端）
 */
@Mapper
public interface UserPrivateChargeStationMapper {

        /**
         * 新增私桩信息
         * 
         * @param privateChargeStation 私桩信息
         * @return 影响的行数
         */
        int insert(PrivateChargeStation privateChargeStation);

        /**
         * 根据用户ID查询私桩列表
         * 
         * @param ownerId 用户ID
         * @return 私桩列表
         */
        List<PrivateStationListVO> selectPrivateStationListByOwnerId(@Param("ownerId") Integer ownerId);

        /**
         * 解绑私桩（软删除）
         * 
         * @param id      私桩ID
         * @param ownerId 用户ID
         * @return 影响的行数
         */
        int unbindPrivateStation(@Param("id") Integer id, @Param("ownerId") Integer ownerId);

        /**
         * 更新私桩固定价格
         * 
         * @param id      私桩ID
         * @param ownerId 用户ID
         * @param price   固定价格
         * @return 影响的行数
         */
        int updatePrivateStationPrice(@Param("id") Integer id, @Param("ownerId") Integer ownerId,
                        @Param("price") Double price);

        /**
         * 根据用户ID和私桩ID查询私桩时间配置
         * 
         * @param ownerId 用户ID
         * @param id      私桩ID
         * @return 私桩时间配置信息
         */
        PrivateStationTimeConfigVO selectPrivateStationTimeConfigByOwnerIdAndStationId(
                        @Param("ownerId") Integer ownerId, @Param("id") Integer id);

        /**
         * 更新私桩启停时间配置
         * 
         * @param id        私桩ID
         * @param ownerId   用户ID
         * @param openTime  每日开启时间
         * @param closeTime 每日关闭时间
         * @return 影响的行数
         */
        int updatePrivateStationTimeConfig(@Param("id") Integer id, @Param("ownerId") Integer ownerId,
                        @Param("openTime") String openTime, @Param("closeTime") String closeTime);

        /**
         * 更新私桩状态（手动开关）
         * 
         * @param id         私桩ID
         * @param ownerId    用户ID
         * @param openStatus 开关状态，0=关闭，1=开启
         * @return 影响的行数
         */
        int updatePrivateStationStatus(@Param("id") Integer id, @Param("ownerId") Integer ownerId,
                        @Param("openStatus") Integer openStatus);

        /**
         * 根据私桩ID查询私桩信息
         *
         * @param id 私桩ID
         * @return 私桩信息
         */
        PrivateChargeStation selectPrivateStationById(@Param("id") Integer id);

        /**
         * 更新私桩信息并重置认证状态
         *
         * @param id                    私桩ID
         * @param ownerId               用户ID
         * @param privateStationAuthDTO 私桩认证请求参数
         * @return 影响的行数
         */
        int updatePrivateStationInfo(@Param("id") Integer id, @Param("ownerId") Integer ownerId,
                        @Param("dto") PrivateStationAuthDTO privateStationAuthDTO);

        /**
         * 根据经纬度范围和关键词查询私桩
         *
         * @param keyword 搜索关键词（可选）
         * @param minLng  最小经度
         * @param maxLng  最大经度
         * @param minLat  最小纬度
         * @param maxLat  最大纬度
         * @return 私桩列表
         */
        List<NaviPrivateStationVO> selectPrivateStationsByGeoRange(@Param("keyword") String keyword,
                        @Param("minLng") Double minLng, @Param("maxLng") Double maxLng,
                        @Param("minLat") Double minLat, @Param("maxLat") Double maxLat);

        /**
         * 更新私桩评分
         *
         * @param id     私桩ID
         * @param rating 评分
         * @return 影响的行数
         */
        int updatePrivateStationRating(@Param("id") Integer id, @Param("rating") Double rating);

        /**
         * 更新私桩定价类型
         *
         * @param id        私桩ID
         * @param ownerId   用户ID
         * @param priceType 定价类型：0-固定价格，1-动态价格
         * @return 影响的行数
         */
        int updatePriceType(@Param("id") Integer id, @Param("ownerId") Integer ownerId,
                        @Param("priceType") Integer priceType);
}