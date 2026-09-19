package com.baffang.user.mapper;

import com.baffang.common.entity.PrivateStationReserve;
import com.baffang.user.vo.PrivateStationReserveListVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 私桩预约Mapper接口（用户端）
 */
@Mapper
public interface UserPrivateStationReserveMapper {
        /**
         * 新增私桩预约记录
         *
         * @param privateStationReserve 私桩预约信息
         * @return 影响的行数
         */
        int insert(PrivateStationReserve privateStationReserve);

        /**
         * 取消私桩预约
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         * @return 影响的行数
         */
        int cancelPrivateStationReserve(@Param("userId") Integer userId, @Param("stationId") Integer stationId);

        /**
         * 查询个人私桩预约记录
         *
         * @param userId 用户ID
         * @param status 预约状态（可选）
         * @return 预约记录列表
         */
        List<com.baffang.user.vo.PrivateStationReserveListVO> queryPrivateStationReserveList(
                        @Param("userId") Integer userId, @Param("status") Integer status);

        /**
         * 查询私桩预约记录（桩主视角）
         *
         * @param stationOwnerId 私桩所有者ID
         * @param status         预约状态（可选）
         * @return 预约记录列表
         */
        List<com.baffang.user.vo.PrivateStationOwnerReserveListVO> queryPrivateStationReserveByOwner(
                        @Param("stationOwnerId") Integer stationOwnerId,
                        @Param("status") Integer status);

        /**
         * 处理私桩预约申请
         *
         * @param userId    用户ID（桩主ID）
         * @param reserveId 预约ID
         * @param status    操作类型：1=确认，2=拒绝
         * @return 影响的行数
         */
        int handlePrivateStationReserve(@Param("userId") Integer userId, @Param("reserveId") Integer reserveId,
                        @Param("status") Integer status);

        /**
         * 根据预约ID查询预约记录
         *
         * @param reserveId 预约ID
         * @return 预约记录
         */
        com.baffang.common.entity.PrivateStationReserve selectPrivateStationReserveById(
                        @Param("reserveId") Integer reserveId);
}