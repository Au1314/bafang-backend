package com.baffang.user.mapper;

import com.baffang.common.entity.EmergencyCall;
import com.baffang.common.entity.VToV;
import com.baffang.user.vo.CallListVO;
import com.baffang.user.vo.CallResponseVO;
import com.baffang.user.vo.EmergencyCallListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * V2V呼叫Mapper接口
 */
@Mapper
public interface UserCallMapper {

        /**
         * 插入V2V呼叫记录
         * 
         * @param vToV V2V呼叫实体
         * @return 插入结果
         */
        int insertV2VCall(VToV vToV);

        /**
         * 查询用户是否存在活跃中的呼叫（状态不为2或3）
         * 
         * @param requestUserId 请求用户ID
         * @return 活跃呼叫数量
         */
        int countActiveCallsByUserId(Integer requestUserId);

        /**
         * 将用户的待响应（0）和已响应（1）的呼叫设为已取消（3）
         * 
         * @param requestUserId 请求用户ID
         * @return 更新的记录数
         */
        int updateCallStatusToCanceled(Integer requestUserId);

        /**
         * 查询指定经纬度范围内的待响应呼叫
         * 
         * @param minLng 最小经度
         * @param maxLng 最大经度
         * @param minLat 最小纬度
         * @param maxLat 最大纬度
         * @return 待响应呼叫列表
         */
        List<CallResponseVO> selectPendingCallsByGeoRange(@Param("minLng") Double minLng,
                        @Param("maxLng") Double maxLng,
                        @Param("minLat") Double minLat, @Param("maxLat") Double maxLat);

        /**
         * 查询所有待响应呼叫的完整信息
         * 
         * @return 所有待响应呼叫列表
         */
        List<VToV> selectAllPendingCalls();

        /**
         * 根据ID查询呼叫记录
         * 
         * @param callId 呼叫ID
         * @return 呼叫记录
         */
        VToV selectCallById(Integer callId);

        /**
         * 更新呼叫响应信息
         * 
         * @param vToV 呼叫响应信息
         * @return 更新的记录数
         */
        int updateCallResponse(VToV vToV);

        /**
         * 查询用户的V2V呼叫记录
         * 
         * @param userId 用户ID
         * @param role   角色：0 = 发起者，1 = 响应者（可选）
         * @param status 状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消（可选）
         * @return 呼叫记录列表
         */
        List<CallListVO> selectCallRecords(@Param("userId") Integer userId, @Param("role") Integer role,
                        @Param("status") Integer status);

        /**
         * 插入紧急呼叫记录
         * 
         * @param emergencyCall 紧急呼叫实体
         * @return 插入结果
         */
        int insertEmergencyCall(EmergencyCall emergencyCall);

        /**
         * 查询用户是否存在活跃中的紧急呼叫（状态不为2或3）
         * 
         * @param requestUserId 请求用户ID
         * @return 活跃紧急呼叫数量
         */
        int countActiveEmergencyCallsByUserId(Integer requestUserId);

        /**
         * 将用户所有待响应（0）和已响应（1）的紧急呼叫设为已取消（3）
         * 
         * @param requestUserId 请求用户ID
         * @return 更新的记录数
         */
        int updateEmergencyCallStatusToCanceled(Integer requestUserId);

        /**
         * 查询用户的紧急呼叫记录
         * 
         * @param userId   用户ID
         * @param callType 呼叫类型：0 = 拖车，1 = 充电桩，2 = 其他（可选）
         * @param status   状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消（可选）
         * @return 紧急呼叫记录列表
         */
        List<EmergencyCallListVO> selectEmergencyCallRecords(@Param("userId") Integer userId,
                        @Param("callType") Integer callType,
                        @Param("status") Integer status);

        /**
         * 查询单个紧急呼叫进度信息
         * 
         * @param userId  用户ID（可选，用于验证权限）
         * @param adminId 管理员ID（可选，用于验证权限）
         * @return 紧急呼叫进度信息
         */
        EmergencyCallListVO selectEmergencyCallProgress(@Param("userId") Integer userId,
                        @Param("adminId") Integer adminId);
}
