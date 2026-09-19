package com.baffang.admin.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baffang.admin.dto.PrivateStationQueryDTO;
import com.baffang.admin.vo.StationAuthDetailVO;
import com.baffang.admin.vo.StationAuthListVO;
import com.baffang.admin.dto.PrivateStationListDTO;
import com.baffang.admin.dto.PrivateStationDetailDTO;

@Mapper
public interface PrivateChargeStationMapper {

    // ========== 原有方法保持不变 ==========

    /**
     * 统计已认证私桩数量
     */
    int countAuthenticatedStations();

    /**
     * 按认证状态统计私桩数量
     */
    int countByAuthStatus(@Param("authStatus") Integer authStatus);

    /**
     * 统计私桩总数
     */
    int countTotal();

    /**
     * 按状态分组统计
     */
    List<Map<String, Object>> countGroupByStatus();

    /**
     * 按类型分组统计
     */
    List<Map<String, Object>> countGroupByType();

    /**
     * 按电流类型分组统计
     */
    List<Map<String, Object>> countGroupByCurrentType();

    /**
     * 按认证状态分组统计
     */
    List<Map<String, Object>> countGroupByAuthStatus();

    /**
     * 按开放状态分组统计
     */
    List<Map<String, Object>> countGroupByOpenStatus();

    /**
     * 查询待审核/审核中的私桩列表
     */
    List<StationAuthListVO> selectAuthList(@Param("authStatus") Integer authStatus);

    /**
     * 查询私桩认证详情
     */
    StationAuthDetailVO selectAuthDetail(@Param("id") Integer id);

    /**
     * 更新认证状态
     */
    int updateAuthStatus(@Param("id") Integer id, @Param("authStatus") Integer authStatus);

    /**
     * 根据ID查询私桩
     */
    Map<String, Object> selectById(@Param("id") Integer id);

    // ========== 新增：私桩信息管理方法 ==========

    /**
     * 分页查询私桩列表（带条件筛选）
     * @param query 查询条件
     * @param offset 偏移量
     * @param limit 每页条数
     */
    List<PrivateStationListDTO> selectPageList(
            @Param("query") PrivateStationQueryDTO query,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 统计符合条件的私桩数量
     */
    Long countByCondition(@Param("query") PrivateStationQueryDTO query);

    /**
     * 查询私桩详情（关联用户信息）
     */
    PrivateStationDetailDTO selectDetailById(@Param("id") Integer id);

    /**
     * 更新私桩信息
     */
    int updateStation(@Param("id") Integer id, @Param("station") Map<String, Object> station);

    /**
     * 软删除私桩
     */
    int softDeleteById(@Param("id") Integer id);

    /**
     * 批量软删除私桩
     */
    int batchSoftDelete(@Param("ids") List<Integer> ids);

    /**
     * 检查私桩是否存在
     */
    int checkExists(@Param("id") Integer id);

}