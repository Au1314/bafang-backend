package com.baffang.user.service.impl;

import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.common.entity.PrivateStationReserve;
import com.baffang.common.enums.entityEnum.PrivateStationReserveStatusEnum;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.result.PageResult;
import com.baffang.user.dto.PrivateStationReserveDTO;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.mapper.UserPrivateStationReserveMapper;
import com.baffang.user.service.PrivateStationUseService;
import com.baffang.user.vo.PrivateStationReserveListVO;
import com.baffang.user.vo.PrivateStationReserveVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 私桩使用服务实现类
 */
@Slf4j
@Service
public class PrivateStationUseServiceImpl implements PrivateStationUseService {

    @Autowired
    private UserPrivateChargeStationMapper privateChargeStationMapper;

    @Autowired
    private UserPrivateStationReserveMapper privateStationReserveMapper;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public PrivateStationReserveVO reservePrivateStation(Integer userId, Integer stationId,
            PrivateStationReserveDTO reserveDTO) {
        // 1. 查询私桩信息
        PrivateChargeStation privateStation = privateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateStation == null) {
            throw new BusinessException(ErrorMsg.PRIVATE_STATION_NOT_EXIST);
        }

        // 2. 解析预约时间
        Date reserveTime;
        try {
            reserveTime = dateFormat.parse(reserveDTO.getReserveTime());
        } catch (ParseException e) {
            throw new BusinessException(ErrorMsg.INVALID_TIME_FORMAT);
        }

        // 3. 桩可预约状态校验（并发控制整改：此前无任何状态校验）
        if (privateStation.getAuthStatus() == null || privateStation.getAuthStatus() != 2) {
            throw new BusinessException("该私桩未通过认证，暂不可预约");
        }
        if (privateStation.getOpenStatus() == null || privateStation.getOpenStatus() != 1) {
            throw new BusinessException("该私桩当前未开放，暂不可预约");
        }

        // 4. 时段冲突检测（防超卖：同桩 60 分钟窗口内已有未完结预约则拒绝；
        //    演进项：Redis 分布式锁消除读-写竞态窗口，当前以 DB 冲突计数兜底）
        if (privateStationReserveMapper.countTimeConflict(stationId, reserveTime) > 0) {
            throw new BusinessException("该时段已有预约（60 分钟内存在冲突），请选择其他时间");
        }

        // 5. 创建预约记录
        PrivateStationReserve reserve = new PrivateStationReserve();
        reserve.setUserId(userId);
        reserve.setStationOwnerId(privateStation.getOwnerId());
        reserve.setChargingStationId(stationId);
        reserve.setReserveTime(reserveTime);
        reserve.setStatus(PrivateStationReserveStatusEnum.PENDING_CONFIRMATION.getCode());

        // 4. 保存预约记录
        int result = privateStationReserveMapper.insert(reserve);
        if (result != 1) {
            throw new BusinessException(ErrorMsg.RESERVE_CREATE_FAILED);
        }

        // 6. 构建返回结果
        PrivateStationReserveVO reserveVO = new PrivateStationReserveVO();
        reserveVO.setReserveId(reserve.getId());
        reserveVO.setStatus(reserve.getStatus());

        return reserveVO;
    }

    @Override
    public int releaseExpiredReservations() {
        // 超时自动释放：待确认且预约时间已过 → 已取消（防止桩位被僵尸预约长期占用）
        int released = privateStationReserveMapper.releaseExpiredReservations();
        if (released > 0) {
            log.info("超时预约自动释放：{} 条", released);
        }
        return released;
    }

    @Override
    public void cancelPrivateStationReserve(Integer userId, Integer stationId) {
        // 调用Mapper方法取消预约
        int result = privateStationReserveMapper.cancelPrivateStationReserve(userId, stationId);

        // 检查是否取消成功
        if (result == 0) {
            throw new BusinessException(ErrorMsg.RESERVE_NOT_FOUND_OR_CANNOT_CANCEL);
        }
    }

    @Override
    public PageResult<PrivateStationReserveListVO> queryPrivateStationReserveList(Integer userId, Integer status,
            Integer pageNum, Integer pageSize) {
        // 设置默认分页参数
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 调用Mapper方法查询预约记录
        List<PrivateStationReserveListVO> list = privateStationReserveMapper.queryPrivateStationReserveList(userId,
                status);

        // 封装分页结果
        PageInfo<PrivateStationReserveListVO> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                (int) pageInfo.getTotal(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                list);
    }
}