package com.baffang.admin.service.impl;

import com.baffang.admin.mapper.CarAuthMapper;
import com.baffang.admin.mapper.PrivateChargeStationMapper;
import com.baffang.admin.service.StatsService;
import com.baffang.admin.vo.PlatformStatsVO;
import com.baffang.admin.vo.StationDistributionVO;
import com.baffang.admin.vo.StationDistributionVO.AuthStatusDistribution;
import com.baffang.admin.vo.StationDistributionVO.CurrentTypeDistribution;
import com.baffang.admin.vo.StationDistributionVO.OpenStatusDistribution;
import com.baffang.admin.vo.StationDistributionVO.StatusDistribution;
import com.baffang.admin.vo.StationDistributionVO.TypeDistribution;
import com.baffang.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PrivateChargeStationMapper privateChargeStationMapper;

    @Autowired
    private CarAuthMapper carAuthMapper;

    // 状态名称映射
    private static final String[] STATUS_NAMES = {"空闲", "已预约", "已充电", "异常"};
    private static final String[] TYPE_NAMES = {"普通私桩", "专业私桩"};
    private static final String[] CURRENT_TYPE_NAMES = {"直流电", "交流电"};
    private static final String[] AUTH_STATUS_NAMES = {"未认证", "审核中", "已认证", "认证失败"};
    private static final String[] OPEN_STATUS_NAMES = {"关闭", "开启"};


    @Override
    public PlatformStatsVO getPlatformStats() {
        PlatformStatsVO statsVO = new PlatformStatsVO();
        
        // 获取今天日期
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 用户总数
        int totalUsers = userMapper.countTotalUsers();
        statsVO.setTotalUsers(totalUsers);
        
        // 今日活跃用户数
        int dailyActiveUsers = userMapper.countDailyActiveUsers(today);
        statsVO.setDailyActiveUsers(dailyActiveUsers);


        // 已认证私桩数量
        int authenticatedStations = privateChargeStationMapper.countAuthenticatedStations();
        statsVO.setAuthenticatedStations(authenticatedStations);
        
        // 已认证车主数量
        int authenticatedCars = carAuthMapper.countAuthenticatedCars();
        statsVO.setAuthenticatedCars(authenticatedCars);
        
        // 统计日期
        statsVO.setStatsDate(today);
        
        return statsVO;
    }
    @Override
    public StationDistributionVO getStationDistribution() {
        StationDistributionVO distributionVO = new StationDistributionVO();

        // 按状态分布
        List<Map<String, Object>> statusData = privateChargeStationMapper.countGroupByStatus();
        List<StatusDistribution> statusList = new ArrayList<>();
        for (Map<String, Object> item : statusData) {
            StatusDistribution sd = new StatusDistribution();
            Integer status = ((Number) item.get("status")).intValue();
            sd.setStatus(status);
            sd.setStatusName(getStatusName(status, STATUS_NAMES));
            sd.setCount(((Number) item.get("count")).intValue());
            statusList.add(sd);
        }
        distributionVO.setStatusDistribution(statusList);

        // 按类型分布
        List<Map<String, Object>> typeData = privateChargeStationMapper.countGroupByType();
        List<TypeDistribution> typeList = new ArrayList<>();
        for (Map<String, Object> item : typeData) {
            TypeDistribution td = new TypeDistribution();
            Integer type = ((Number) item.get("type")).intValue();
            td.setType(type);
            td.setTypeName(getStatusName(type, TYPE_NAMES));
            td.setCount(((Number) item.get("count")).intValue());
            typeList.add(td);
        }
        distributionVO.setTypeDistribution(typeList);

        // 按电流类型分布
        List<Map<String, Object>> currentTypeData = privateChargeStationMapper.countGroupByCurrentType();
        List<CurrentTypeDistribution> currentTypeList = new ArrayList<>();
        for (Map<String, Object> item : currentTypeData) {
            CurrentTypeDistribution ctd = new CurrentTypeDistribution();
            Integer currentType = ((Number) item.get("currentType")).intValue();
            ctd.setCurrentType(currentType);
            ctd.setCurrentTypeName(getStatusName(currentType, CURRENT_TYPE_NAMES));
            ctd.setCount(((Number) item.get("count")).intValue());
            currentTypeList.add(ctd);
        }
        distributionVO.setCurrentTypeDistribution(currentTypeList);

        // 按认证状态分布
        List<Map<String, Object>> authStatusData = privateChargeStationMapper.countGroupByAuthStatus();
        List<AuthStatusDistribution> authStatusList = new ArrayList<>();
        for (Map<String, Object> item : authStatusData) {
            AuthStatusDistribution asd = new AuthStatusDistribution();
            Integer authStatus = ((Number) item.get("authStatus")).intValue();
            asd.setAuthStatus(authStatus);
            asd.setAuthStatusName(getStatusName(authStatus, AUTH_STATUS_NAMES));
            asd.setCount(((Number) item.get("count")).intValue());
            authStatusList.add(asd);
        }
        distributionVO.setAuthStatusDistribution(authStatusList);

        // 按开放状态分布
        List<Map<String, Object>> openStatusData = privateChargeStationMapper.countGroupByOpenStatus();
        List<OpenStatusDistribution> openStatusList = new ArrayList<>();
        for (Map<String, Object> item : openStatusData) {
            OpenStatusDistribution osd = new OpenStatusDistribution();
            Integer openStatus = ((Number) item.get("openStatus")).intValue();
            osd.setOpenStatus(openStatus);
            osd.setOpenStatusName(getStatusName(openStatus, OPEN_STATUS_NAMES));
            osd.setCount(((Number) item.get("count")).intValue());
            openStatusList.add(osd);
        }
        distributionVO.setOpenStatusDistribution(openStatusList);

        return distributionVO;
    }

    private String getStatusName(Integer status, String[] names) {
        if (status == null || status < 0 || status >= names.length) {
            return "未知";
        }
        return names[status];
    }
}
