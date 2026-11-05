package com.baffang.admin.service.impl;

import com.baffang.admin.dto.*;
import com.baffang.admin.mapper.AdminMapper;
import com.baffang.admin.mapper.EmergencyCallMapper;
import com.baffang.admin.mapper.EmergencyHandleLogMapper;
import com.baffang.admin.service.AdminNoticeService;
import com.baffang.admin.service.AmapService;
import com.baffang.admin.service.EmergencyCallService;
import com.baffang.admin.vo.*;
import com.baffang.common.entity.AdminInfo;
import com.baffang.common.entity.EmergencyHandleLog;
import com.baffang.common.exception.BusinessException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmergencyCallServiceImpl implements EmergencyCallService {

    private final EmergencyCallMapper emergencyCallMapper;
    private final AmapService amapService;
    private final AdminNoticeService noticeService;  // 新增
    private final AdminMapper adminMapper;  // 新增，用于获取客服姓名
    private final EmergencyHandleLogMapper emergencyHandleLogMapper;  // 处理日志持久化

    // ==================== 待响应列表（支持导航距离排序） ====================

    @Override
    public Page<EmergencyCallListVO> getPendingList(EmergencyCallPendingQueryDTO queryDTO) {
        // 1. 先用直线距离查询，获取候选列表
        PageHelper.startPage(1, 100); // 先获取较多数据用于计算
        List<Map<String, Object>> mapList = emergencyCallMapper.selectPendingListByDistance(queryDTO);

        // 2. 转换为VO
        List<EmergencyCallListVO> voList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            EmergencyCallListVO vo = convertToListVO(map);
            // 设置直线距离
            Double straightDistance = getDouble(map, "distance");
            if (straightDistance != null) {
                vo.setDistance(new BigDecimal(straightDistance).setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
            voList.add(vo);
        }

        // 3. 如果选择驾车导航距离排序
        if (queryDTO.getDistanceType() != null && queryDTO.getDistanceType() == 1) {
            voList = calculateAndSortByDrivingDistance(voList, queryDTO.getLongitude(), queryDTO.getLatitude());
        }

        // 4. 按最大距离过滤
        final Double maxDistance = queryDTO.getMaxDistance();
        if (maxDistance != null) {
            if (queryDTO.getDistanceType() != null && queryDTO.getDistanceType() == 1) {
                // 按驾车距离过滤
                voList = voList.stream()
                    .filter(vo -> vo.getDrivingDistance() != null && vo.getDrivingDistance() <= maxDistance)
                    .collect(Collectors.toList());
            } else {
                // 按直线距离过滤（已在SQL中过滤，这里再次确认）
                voList = voList.stream()
                    .filter(vo -> vo.getDistance() != null && vo.getDistance() <= maxDistance)
                    .collect(Collectors.toList());
            }
        }

        // 5. 手动分页（修复：增加null校验+兜底）
    int pageNum = (queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1) ? 1 : queryDTO.getPageNum();
    int pageSize = (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 || queryDTO.getPageSize() > 100) ? 10 : queryDTO.getPageSize();
    int total = voList.size();
    int fromIndex = (pageNum - 1) * pageSize;
    int toIndex = Math.min(fromIndex + pageSize, total);

    Page<EmergencyCallListVO> page = new Page<>(pageNum, pageSize);
    page.setTotal(total);
    page.setPages((total + pageSize - 1) / pageSize);

    if (fromIndex < total) {
        page.addAll(voList.subList(fromIndex, toIndex));
    }

    return page;
    }

    /**
     * 计算驾车导航距离并排序
     */
    private List<EmergencyCallListVO> calculateAndSortByDrivingDistance(
            List<EmergencyCallListVO> voList, Double adminLng, Double adminLat) {

        if (voList.isEmpty()) {
            return voList;
        }

        log.info("开始计算{}个呼叫点的驾车导航距离", voList.size());

        // 构建终点列表
        List<double[]> destinations = voList.stream()
            .filter(vo -> vo.getCallLongitude() != null && vo.getCallLatitude() != null)
            .map(vo -> new double[]{vo.getCallLongitude(), vo.getCallLatitude()})
            .collect(Collectors.toList());

        // 调用高德API批量计算
        try {
            List<Long> distances = amapService.getBatchDrivingDistance(adminLng, adminLat, destinations);

            // 填充驾车距离
            int index = 0;
            for (EmergencyCallListVO vo : voList) {
                if (vo.getCallLongitude() != null && vo.getCallLatitude() != null && index < distances.size()) {
                    Long distanceMeters = distances.get(index);
                    if (distanceMeters != null) {
                        // 转换为公里，保留2位小数
                        vo.setDrivingDistance(
                            new BigDecimal(distanceMeters).divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP).doubleValue()
                        );
                    }
                    index++;
                }
            }

            // 按驾车距离排序
            voList.sort((a, b) -> {
                if (a.getDrivingDistance() == null && b.getDrivingDistance() == null) return 0;
                if (a.getDrivingDistance() == null) return 1;
                if (b.getDrivingDistance() == null) return -1;
                return Double.compare(a.getDrivingDistance(), b.getDrivingDistance());
            });

        } catch (Exception e) {
            log.error("计算驾车距离失败，使用直线距离排序", e);
            // 失败时按直线距离排序
            voList.sort(Comparator.comparing(EmergencyCallListVO::getDistance,
                Comparator.nullsLast(Comparator.naturalOrder())));
        }

        return voList;
    }

    // ==================== 呼叫列表（全部） ====================

    @Override
public Page<EmergencyCallListVO> getCallList(EmergencyCallQueryDTO queryDTO) {
    // 修复：使用DTO的兜底方法getPage()/getSize()，避免null
    PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
    List<Map<String, Object>> mapList = emergencyCallMapper.selectList(queryDTO);

    Page<EmergencyCallListVO> page = new Page<>();
    page.setTotal(((Page<?>) mapList).getTotal());
    page.setPages(((Page<?>) mapList).getPages());
    // 同步修改：用兜底后的页码/条数
    page.setPageNum(queryDTO.getPage());
    page.setPageSize(queryDTO.getSize());

    for (Map<String, Object> map : mapList) {
        page.add(convertToListVO(map));
    }

    return page;
}

    // ==================== 呼叫详情 ====================

    @Override
    public EmergencyCallDetailVO getCallDetail(Integer id) {
        Map<String, Object> map = emergencyCallMapper.selectDetailById(id);
        if (map == null || map.isEmpty()) {
            throw new BusinessException("呼叫记录不存在");
        }
        return convertToDetailVO(map);
    }

    // ==================== 响应紧急呼叫 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void respondCall(Integer id, EmergencyRespondDTO dto, Integer adminId) {
        // 1. 校验呼叫是否存在
        if (!emergencyCallMapper.existsById(id)) {
            throw new BusinessException("呼叫记录不存在");
        }

        // 2. 校验状态
        Integer status = emergencyCallMapper.selectStatusById(id);
        if (status == null) {
            throw new BusinessException("呼叫记录不存在");
        }
        if (status != 0) {
            throw new BusinessException("该呼叫已被响应或已结束");
        }

        // 3. 检查响应员是否有正在处理的呼叫
        if (emergencyCallMapper.hasActiveCall(adminId)) {
            throw new BusinessException("您有正在处理的呼叫，请先完成");
        }

        // 4. 响应呼叫
        int updated = emergencyCallMapper.respond(id, adminId);
        if (updated == 0) {
            throw new BusinessException("响应失败，请刷新重试");
        }

        log.info("响应员[{}]响应呼叫[{}]，位置: ({}, {})", adminId, id, dto.getLongitude(), dto.getLatitude());
        // 5. 发送通知给用户
        sendRespondNotice(id, adminId);
    }

    // ==================== 更新目标位置 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTarget(Integer id, EmergencyTargetDTO dto, Integer adminId) {
        // 1. 校验呼叫是否存在
        if (!emergencyCallMapper.existsById(id)) {
            throw new BusinessException("呼叫记录不存在");
        }

        // 2. 校验是否为该呼叫的响应员
        Integer responseAdminId = emergencyCallMapper.selectResponseAdminId(id);
        if (responseAdminId == null || !responseAdminId.equals(adminId)) {
            throw new BusinessException("您不是该呼叫的响应员");
        }

        // 3. 校验状态
        Integer status = emergencyCallMapper.selectStatusById(id);
        if (status != 1) {
            throw new BusinessException("只能在响应中状态下更新目标位置");
        }

        // 4. 更新目标位置
        emergencyCallMapper.updateTarget(id, dto.getTargetPosition(),
                dto.getTargetLongitude(), dto.getTargetLatitude());

        log.info("响应员[{}]更新呼叫[{}]的目标位置: {}", adminId, id, dto.getTargetPosition());
    }

    // ==================== 完成呼叫 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeCall(Integer id, EmergencyCompleteDTO dto, Integer adminId) {
        // 1. 校验呼叫是否存在
        if (!emergencyCallMapper.existsById(id)) {
            throw new BusinessException("呼叫记录不存在");
        }

        // 2. 校验是否为该呼叫的响应员
        Integer responseAdminId = emergencyCallMapper.selectResponseAdminId(id);
        if (responseAdminId == null || !responseAdminId.equals(adminId)) {
            throw new BusinessException("您不是该呼叫的响应员");
        }

        // 3. 校验状态
        Integer status = emergencyCallMapper.selectStatusById(id);
        if (status != 1) {
            throw new BusinessException("只能完成响应中的呼叫");
        }

        // 4. 完成呼叫
        int updated = emergencyCallMapper.complete(id);
        if (updated == 0) {
            throw new BusinessException("操作失败，请刷新重试");
        }

        log.info("响应员[{}]完成呼叫[{}]，备注: {}", adminId, id, dto != null ? dto.getRemark() : "");
        sendCompleteNotice(id, dto != null ? dto.getRemark() : null);
    }

    // ==================== 取消呼叫 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCall(Integer id, EmergencyCancelDTO dto, Integer adminId) {
        // 1. 校验呼叫是否存在
        if (!emergencyCallMapper.existsById(id)) {
            throw new BusinessException("呼叫记录不存在");
        }

        // 2. 校验状态
        Integer status = emergencyCallMapper.selectStatusById(id);
        if (status == null) {
            throw new BusinessException("呼叫记录不存在");
        }
        if (status == 2) {
            throw new BusinessException("已完成的呼叫无法取消");
        }
        if (status == 3) {
            throw new BusinessException("该呼叫已被取消");
        }

        // 3. 取消呼叫
        int updated = emergencyCallMapper.cancel(id);
        if (updated == 0) {
            throw new BusinessException("操作失败，请刷新重试");
        }

        log.info("管理员[{}]取消呼叫[{}]，原因: {}", adminId, id, dto.getCancelReason());

        sendCancelNotice(id, dto.getCancelReason());
    }

    // ==================== 处理日志（真持久化） ====================

    @Override
    public void addHandleLog(Integer callId, String content, Integer adminId) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("日志内容不能为空");
        }
        if (!emergencyCallMapper.existsById(callId)) {
            throw new BusinessException("呼叫记录不存在");
        }
        Integer status = emergencyCallMapper.selectStatusById(callId);
        if (status == null || status == 0) {
            throw new BusinessException("请先响应呼叫后再添加处理日志");
        }
        if (status == 2 || status == 3) {
            throw new BusinessException("该呼叫已结束，无法继续添加日志");
        }
        EmergencyHandleLog record = new EmergencyHandleLog();
        record.setCallId(callId);
        record.setAdminId(adminId);
        record.setContent(content.trim());
        emergencyHandleLogMapper.insert(record);
        log.info("响应员[{}]为呼叫[{}]添加处理日志", adminId, callId);
    }

    @Override
    public List<Map<String, Object>> getHandleLogs(Integer callId) {
        if (!emergencyCallMapper.existsById(callId)) {
            throw new BusinessException("呼叫记录不存在");
        }
        return emergencyHandleLogMapper.selectByCallId(callId);
    }

     /**
     * 发送响应通知
     */
    private void sendRespondNotice(Integer callId, Integer adminId) {
        try {
            Integer userId = emergencyCallMapper.selectRequestUserId(callId);
            if (userId == null) return;

            String adminName = getAdminName(adminId);
            noticeService.sendEmergencyNotice(
                    userId,
                    callId,
                    "紧急呼叫已响应",
                    String.format("您的紧急呼叫已被 %s 响应，正在前往您的位置，请保持电话畅通。", adminName)
            );
        } catch (Exception e) {
            log.error("发送紧急呼叫响应通知失败: callId={}", callId, e);
        }
    }

    /**
     * 发送完成通知
     */
    private void sendCompleteNotice(Integer callId, String remark) {
        try {
            Integer userId = emergencyCallMapper.selectRequestUserId(callId);
            if (userId == null) return;

            String content = "您的紧急呼叫已处理完成。";
            if (remark != null && !remark.isEmpty()) {
                content += "处理备注：" + remark;
            }

            noticeService.sendEmergencyNotice(
                    userId,
                    callId,
                    "紧急呼叫已完成",
                    content
            );
        } catch (Exception e) {
            log.error("发送紧急呼叫完成通知失败: callId={}", callId, e);
        }
    }

    /**
     * 发送取消通知
     */
    private void sendCancelNotice(Integer callId, String reason) {
        try {
            Integer userId = emergencyCallMapper.selectRequestUserId(callId);
            if (userId == null) return;

            noticeService.sendEmergencyNotice(
                    userId,
                    callId,
                    "紧急呼叫已取消",
                    String.format("您的紧急呼叫已被取消。原因：%s", reason != null ? reason : "无")
            );
        } catch (Exception e) {
            log.error("发送紧急呼叫取消通知失败: callId={}", callId, e);
        }
    }

    /**
     * 获取管理员姓名
     */
    private String getAdminName(Integer adminId) {
        try {
            AdminInfo admin = adminMapper.selectById(adminId.longValue());
            return admin != null ? admin.getName() : "客服";
        } catch (Exception e) {
            return "客服";
        }
    }

    // ==================== 获取我的响应记录 ====================

    @Override
    public Page<EmergencyCallListVO> getMyResponses(Integer adminId, Integer status,
                                                     String startTime, String endTime,
                                                     Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> mapList = emergencyCallMapper.selectMyResponses(adminId, status, startTime, endTime);

        Page<EmergencyCallListVO> page = new Page<>();
        page.setTotal(((Page<?>) mapList).getTotal());
        page.setPages(((Page<?>) mapList).getPages());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);

        for (Map<String, Object> map : mapList) {
            page.add(convertToListVO(map));
        }

        return page;
    }

    // ==================== 获取统计数据 ====================

    @Override
    public EmergencyStatisticsVO getStatistics(Integer adminId) {
        EmergencyStatisticsVO vo = new EmergencyStatisticsVO();

        // 全局统计
        Map<String, Object> stats = emergencyCallMapper.selectStatistics();
        if (stats != null) {
            vo.setTotalCallCount(getIntegerDefault(stats, "totalCallCount", 0));
            vo.setTodayCallCount(getIntegerDefault(stats, "todayCallCount", 0));
            vo.setPendingCount(getIntegerDefault(stats, "pendingCount", 0));
            vo.setRespondingCount(getIntegerDefault(stats, "respondingCount", 0));
            vo.setCompletedCount(getIntegerDefault(stats, "completedCount", 0));
            vo.setCancelledCount(getIntegerDefault(stats, "cancelledCount", 0));
            vo.setAvgResponseTime(getDouble(stats, "avgResponseTime"));
        }

        // 个人统计
        Map<String, Object> myStats = emergencyCallMapper.selectMyStatistics(adminId);
        if (myStats != null) {
            vo.setMyTodayResponseCount(getIntegerDefault(myStats, "myTodayResponseCount", 0));
            vo.setMyTotalResponseCount(getIntegerDefault(myStats, "myTotalResponseCount", 0));
        }

        return vo;
    }

    // ==================== 转换方法 ====================

    private EmergencyCallListVO convertToListVO(Map<String, Object> map) {
        EmergencyCallListVO vo = new EmergencyCallListVO();
        vo.setId(getInteger(map, "id"));
        vo.setRequestUserId(getInteger(map, "requestUserId"));
        vo.setRequestUserName(getString(map, "requestUserName"));

        // 手机号脱敏
        String phone = getString(map, "requestUserPhone");
        vo.setRequestUserPhone(maskPhone(phone));

        vo.setType(getInteger(map, "type"));
        vo.setTypeName(convertTypeName(vo.getType()));
        vo.setResponseAdminId(getInteger(map, "responseAdminId"));
        vo.setResponseAdminName(getString(map, "responseAdminName"));
        vo.setCallRange(getDouble(map, "callRange"));
        vo.setCallTime(getLocalDateTime(map, "callTime"));
        vo.setCallPosition(getString(map, "callPosition"));
        vo.setCallLongitude(getDouble(map, "callLongitude"));
        vo.setCallLatitude(getDouble(map, "callLatitude"));
        vo.setTargetPosition(getString(map, "targetPosition"));
        vo.setStatus(getInteger(map, "status"));
        vo.setStatusName(convertStatusName(vo.getStatus()));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setUpdateTime(getLocalDateTime(map, "updateTime"));
        return vo;
    }

    private EmergencyCallDetailVO convertToDetailVO(Map<String, Object> map) {
        EmergencyCallDetailVO vo = new EmergencyCallDetailVO();
        vo.setId(getInteger(map, "id"));

        // 呼叫用户信息
        vo.setRequestUserId(getInteger(map, "requestUserId"));
        vo.setRequestUserName(getString(map, "requestUserName"));
        vo.setRequestUserPhone(maskPhone(getString(map, "requestUserPhone")));
        vo.setRequestUserAvatar(getString(map, "requestUserAvatar"));

        // 呼叫信息
        vo.setType(getInteger(map, "type"));
        vo.setTypeName(convertTypeName(vo.getType()));
        vo.setCallRange(getDouble(map, "callRange"));
        vo.setCallTime(getLocalDateTime(map, "callTime"));
        vo.setCallPosition(getString(map, "callPosition"));
        vo.setCallLongitude(getDouble(map, "callLongitude"));
        vo.setCallLatitude(getDouble(map, "callLatitude"));

        // 目标位置
        vo.setTargetPosition(getString(map, "targetPosition"));
        vo.setTargetLongitude(getDouble(map, "targetLongitude"));
        vo.setTargetLatitude(getDouble(map, "targetLatitude"));

        // 响应信息
        vo.setResponseAdminId(getInteger(map, "responseAdminId"));
        vo.setResponseAdminName(getString(map, "responseAdminName"));


        // 状态信息
        vo.setStatus(getInteger(map, "status"));
        vo.setStatusName(convertStatusName(vo.getStatus()));
        vo.setCreateTime(getLocalDateTime(map, "createTime"));
        vo.setUpdateTime(getLocalDateTime(map, "updateTime"));

        return vo;
    }

    private String convertTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 0: return "呼叫拖车";
            case 1: return "呼叫充电桩";
            case 2: return "其他呼叫";
            default: return "未知";
        }
    }

    private String convertStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待响应";
            case 1: return "已响应";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知";
        }
    }

    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    // ==================== 工具方法 ====================

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntegerDefault(Map<String, Object> map, String key, Integer defaultValue) {
        Integer result = getInteger(map, key);
        return result != null ? result : defaultValue;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof BigDecimal) return ((BigDecimal) value).doubleValue();
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime getLocalDateTime(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        return null;
    }
}
