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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 私桩使用服务实现类
 */
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

        // 3. 创建预约记录
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

        // 5. 构建返回结果
        PrivateStationReserveVO reserveVO = new PrivateStationReserveVO();
        reserveVO.setReserveId(reserve.getId());
        reserveVO.setStatus(reserve.getStatus());

        return reserveVO;
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