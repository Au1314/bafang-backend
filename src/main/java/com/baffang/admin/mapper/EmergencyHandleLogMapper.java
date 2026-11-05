package com.baffang.admin.mapper;

import com.baffang.common.entity.EmergencyHandleLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 紧急呼叫处理日志 Mapper
 */
@Mapper
public interface EmergencyHandleLogMapper {

    /**
     * 新增处理日志
     */
    int insert(EmergencyHandleLog record);

    /**
     * 按呼叫查询处理日志（含管理员姓名，时间正序）
     */
    List<Map<String, Object>> selectByCallId(@Param("callId") Integer callId);
}
