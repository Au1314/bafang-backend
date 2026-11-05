package com.baffang.admin.service;

import com.baffang.admin.dto.*;
import com.baffang.admin.vo.*;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface EmergencyCallService {

    /**
     * 获取待响应呼叫列表（按距离排序）
     */
    Page<EmergencyCallListVO> getPendingList(EmergencyCallPendingQueryDTO queryDTO);

    /**
     * 获取呼叫列表
     */
    Page<EmergencyCallListVO> getCallList(EmergencyCallQueryDTO queryDTO);

    /**
     * 获取呼叫详情
     */
    EmergencyCallDetailVO getCallDetail(Integer id);

    /**
     * 响应紧急呼叫
     */
    void respondCall(Integer id, EmergencyRespondDTO dto, Integer adminId);

    /**
     * 更新目标位置
     */
    void updateTarget(Integer id, EmergencyTargetDTO dto, Integer adminId);

    /**
     * 完成呼叫
     */
    void completeCall(Integer id, EmergencyCompleteDTO dto, Integer adminId);

    /**
     * 取消呼叫
     */
    void cancelCall(Integer id, EmergencyCancelDTO dto, Integer adminId);

    /**
     * 获取我的响应记录
     */
    Page<EmergencyCallListVO> getMyResponses(Integer adminId, Integer status, 
                                              String startTime, String endTime,
                                              Integer pageNum, Integer pageSize);

    /**
     * 获取统计数据
     */
    EmergencyStatisticsVO getStatistics(Integer adminId);

    /**
     * 添加处理日志（真持久化到 emergency_handle_log 表）
     */
    void addHandleLog(Integer callId, String content, Integer adminId);

    /**
     * 查询呼叫的处理日志（时间正序，含管理员姓名）
     */
    List<Map<String, Object>> getHandleLogs(Integer callId);
}
