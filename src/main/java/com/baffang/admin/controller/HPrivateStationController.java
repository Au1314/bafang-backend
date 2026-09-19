package com.baffang.admin.controller;

import com.baffang.admin.dto.*;
import com.baffang.admin.mapper.PrivateChargeStationMapper;
import com.baffang.common.enums.*;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 私桩信息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/private-station")
public class HPrivateStationController {

    @Autowired
    private PrivateChargeStationMapper stationMapper;

    /**
     * 分页查询私桩
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    public Result<PageResult<PrivateStationListDTO>> getStationPage(PrivateStationQueryDTO query) {
        // 1. 分页参数兜底 + 边界校验（核心修复：避免空指针）
        int pageNum = Optional.ofNullable(query.getPageNum()).orElse(1);
        int pageSize = Optional.ofNullable(query.getPageSize()).orElse(10);
        pageNum = Math.max(pageNum, 1); // 防止pageNum < 1
        pageSize = Math.min(Math.max(pageSize, 1), 100); // 限制最大每页100条

        // 2. 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 3. 查询列表
        List<PrivateStationListDTO> list = stationMapper.selectPageList(query, offset, pageSize);

        // 4. 查询总数
        Long total = stationMapper.countByCondition(query);

        // 5. 填充名称字段
        list.forEach(this::fillListNames);

        // 6. 计算总页数
        int pages = total == 0 ? 0 : (int) Math.ceil((double) total / pageSize);

        // 7. 构建PageResult（适配项目的下划线字段名）
        PageResult<PrivateStationListDTO> pageResult = new PageResult<>();
        pageResult.setTotal(total.intValue());    // 总记录数
        pageResult.setPages(pages);                // 总页数
        pageResult.setPageNum(pageNum);           // 当前页数（下划线字段）
        pageResult.setPageSize(pageSize);         // 页面大小（下划线字段）
        pageResult.setList(list);                  // 数据列表

        // 8. 按项目规范返回Result包装的结果
        return Result.success(pageResult);
    }

    /**
     * 获取私桩详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    public Result<PrivateStationDetailDTO> getStationDetail(@PathVariable Integer id) {
        // 1. 校验ID非空
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        // 2. 查询详情
        PrivateStationDetailDTO detail = stationMapper.selectDetailById(id);

        // 3. 校验是否存在
        if (detail == null) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 4. 填充名称字段
        fillDetailNames(detail);

        return Result.success(detail);
    }

    /**
     * 修改私桩信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    public Result<Void> updateStation(
            @PathVariable Integer id,
            @RequestBody PrivateStationUpdateDTO updateDTO) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }
        if (updateDTO == null) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        // 2. 校验私桩是否存在
        int exists = stationMapper.checkExists(id);
        if (exists == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 3. 构建更新参数
        Map<String, Object> updateMap = buildUpdateMap(updateDTO);

        // 4. 执行更新（无更新参数时直接返回成功）
        if (!updateMap.isEmpty()) {
            int affectRows = stationMapper.updateStation(id, updateMap);
            log.info("私桩 {} 信息已更新，影响行数：{}", id, affectRows);
        }

        return Result.success();
    }

    /**
     * 删除私桩（软删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN:ROLE:SUPER_ADMIN')")
    public Result<Void> deleteStation(@PathVariable Integer id) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        // 2. 校验私桩是否存在
        int exists = stationMapper.checkExists(id);
        if (exists == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 3. 执行软删除
        int affectRows = stationMapper.softDeleteById(id);
        log.info("私桩 {} 已删除，影响行数：{}", id, affectRows);

        return Result.success();
    }

    /**
     * 批量删除私桩
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('ADMIN:ROLE:SUPER_ADMIN')")
    public Result<Void> batchDeleteStations(@RequestBody List<Integer> ids) {
        // 1. 参数校验
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }
        // 过滤无效ID（<=0）
        ids = ids.stream().filter(id -> id != null && id > 0).toList();
        if (ids.isEmpty()) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        // 2. 执行批量删除
        int count = stationMapper.batchSoftDelete(ids);

        log.info("批量删除私桩，请求数量: {}，有效数量: {}，实际删除: {}", 
                ids.size(), ids.size(), count);

        return Result.success();
    }

    /**
     * 私桩统计
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('ADMIN:ROLE:SUPER_ADMIN', 'ADMIN:ROLE:ADMIN')")
    public Result<PrivateStationStatsDTO> getStationStats() {
        PrivateStationStatsDTO stats = new PrivateStationStatsDTO();

        // 1. 总数
        stats.setTotal((long) stationMapper.countTotal());

        // 2. 状态统计
        List<Map<String, Object>> statusList = stationMapper.countGroupByStatus();
        PrivateStationStatsDTO.StatusStats statusStats = new PrivateStationStatsDTO.StatusStats();
        statusStats.setIdle(getCountFromList(statusList, "status", 0));
        statusStats.setReserved(getCountFromList(statusList, "status", 1));
        statusStats.setCharging(getCountFromList(statusList, "status", 2));
        statusStats.setAbnormal(getCountFromList(statusList, "status", 3));
        stats.setStatusStats(statusStats);

        // 3. 认证状态统计
        List<Map<String, Object>> authList = stationMapper.countGroupByAuthStatus();
        PrivateStationStatsDTO.AuthStats authStats = new PrivateStationStatsDTO.AuthStats();
        authStats.setUnauth(getCountFromList(authList, "authStatus", 0));
        authStats.setPending(getCountFromList(authList, "authStatus", 1));
        authStats.setApproved(getCountFromList(authList, "authStatus", 2));
        authStats.setRejected(getCountFromList(authList, "authStatus", 3));
        stats.setAuthStats(authStats);

        // 4. 类型统计
        List<Map<String, Object>> typeList = stationMapper.countGroupByType();
        PrivateStationStatsDTO.TypeStats typeStats = new PrivateStationStatsDTO.TypeStats();
        typeStats.setNormal(getCountFromList(typeList, "type", 0));
        typeStats.setProfessional(getCountFromList(typeList, "type", 1));
        stats.setTypeStats(typeStats);

        return Result.success(stats);
    }

    // ==================== 私有方法 ====================

    /**
     * 构建更新参数 Map
     */
    private Map<String, Object> buildUpdateMap(PrivateStationUpdateDTO updateDTO) {
        Map<String, Object> updateMap = new HashMap<>();

        if (updateDTO.getName() != null) {
            updateMap.put("name", updateDTO.getName());
        }
        if (updateDTO.getLocationName() != null) {
            updateMap.put("locationName", updateDTO.getLocationName());
        }
        if (updateDTO.getLongitude() != null) {
            updateMap.put("longitude", updateDTO.getLongitude());
        }
        if (updateDTO.getLatitude() != null) {
            updateMap.put("latitude", updateDTO.getLatitude());
        }
        if (updateDTO.getPrice() != null) {
            updateMap.put("price", updateDTO.getPrice());
        }
        if (updateDTO.getStatus() != null) {
            updateMap.put("status", updateDTO.getStatus());
        }
        if (updateDTO.getType() != null) {
            updateMap.put("type", updateDTO.getType());
        }
        if (updateDTO.getCurrentType() != null) {
            updateMap.put("currentType", updateDTO.getCurrentType());
        }
        if (updateDTO.getMaxChargeVoltage() != null) {
            updateMap.put("maxChargeVoltage", updateDTO.getMaxChargeVoltage());
        }
        if (updateDTO.getMaxChargeCurrent() != null) {
            updateMap.put("maxChargeCurrent", updateDTO.getMaxChargeCurrent());
        }
        if (updateDTO.getMaxChargePower() != null) {
            updateMap.put("maxChargePower", updateDTO.getMaxChargePower());
        }
        if (updateDTO.getOpenStatus() != null) {
            updateMap.put("openStatus", updateDTO.getOpenStatus());
        }
        if (updateDTO.getImage() != null) {
            updateMap.put("image", updateDTO.getImage());
        }
        if (updateDTO.getDescription() != null) {
            updateMap.put("description", updateDTO.getDescription());
        }
        // ========== 新增 ==========
    if (updateDTO.getPriceType() != null) {
        updateMap.put("priceType", updateDTO.getPriceType());
    }

        return updateMap;
    }

    /**
     * 填充列表名称字段
     */
    private void fillListNames(PrivateStationListDTO dto) {
        dto.setStatusName(StationStatusEnum.getDesc(dto.getStatus()));
        dto.setTypeName(StationTypeEnum.getDesc(dto.getType()));
        dto.setCurrentTypeName(CurrentTypeEnum.getDesc(dto.getCurrentType()));
        dto.setOpenStatusName(dto.getOpenStatus() != null && dto.getOpenStatus() == 1 ? "开启" : "关闭");
        dto.setAuthStatusName(StationAuthStatusEnum.getDesc(dto.getAuthStatus()));

        // ========== 新增 ==========
    dto.setPriceTypeName(PriceTypeEnum.getDesc(dto.getPriceType()));

        // 修复：手机号脱敏 - 判空 + 长度校验，避免索引越界
        if (dto.getOwnerPhone() != null && dto.getOwnerPhone().trim().length() == 11) {
            String phone = dto.getOwnerPhone().trim();
            dto.setOwnerPhone(phone.substring(0, 3) + "****" + phone.substring(7));
        }
    }

    /**
     * 填充详情名称字段
     */
    private void fillDetailNames(PrivateStationDetailDTO dto) {
        dto.setStatusName(StationStatusEnum.getDesc(dto.getStatus()));
        dto.setTypeName(StationTypeEnum.getDesc(dto.getType()));
        dto.setCurrentTypeName(CurrentTypeEnum.getDesc(dto.getCurrentType()));
        dto.setOpenStatusName(dto.getOpenStatus() != null && dto.getOpenStatus() == 1 ? "开启" : "关闭");
        dto.setAuthStatusName(StationAuthStatusEnum.getDesc(dto.getAuthStatus()));
          // ========== 新增 ==========
    dto.setPriceTypeName(PriceTypeEnum.getDesc(dto.getPriceType()));
    }

    /**
     * 从统计 List 中获取数量（兼容类型转换 + 空值）
     */
    private Long getCountFromList(List<Map<String, Object>> list, String keyName, Integer keyValue) {
        if (list == null || list.isEmpty()) return 0L;
        for (Map<String, Object> map : list) {
            Object key = map.get(keyName);
            if (key != null) {
                int keyInt;
                try {
                    // 兼容 Integer/Long 类型的 key 值
                    keyInt = Integer.parseInt(key.toString());
                } catch (NumberFormatException e) {
                    continue;
                }
                if (keyInt == keyValue) {
                    Object count = map.get("count");
                    return count != null ? ((Number) count).longValue() : 0L;
                }
            }
        }
        return 0L;
    }
}