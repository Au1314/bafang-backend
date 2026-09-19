package com.baffang.admin.mapper;

import com.baffang.admin.dto.SystemLogQueryDTO;
import com.baffang.admin.vo.SystemLogDetailVO;
import com.baffang.admin.vo.SystemLogListVO;
import com.baffang.common.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface SystemLogMapper {

    /**
     * 插入日志
     */
    int insert(SystemLog systemLog);

    /**
     * 分页查询日志
     */
    List<SystemLog> selectPageList(
            @Param("module") String module,
            @Param("action") String action,
            @Param("status") Integer status,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 统计数量
     */
    Long countByCondition(
            @Param("module") String module,
            @Param("action") String action,
            @Param("status") Integer status,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );

    /**
     * 按模块统计
     */
    List<Map<String, Object>> countGroupByModule();

    /**
     * 按日期统计
     */
    List<Map<String, Object>> countGroupByDate(@Param("days") Integer days);


    /**
     * 查询日志列表
     */
    List<SystemLogListVO> selectList(@Param("query") SystemLogQueryDTO query);
    
    /**
     * 查询日志详情
     */
    SystemLogDetailVO selectDetailById(@Param("id") Long id);
    
    /**
     * 获取所有模块名称（用于下拉筛选）
     */
    List<String> selectDistinctModules();
}
