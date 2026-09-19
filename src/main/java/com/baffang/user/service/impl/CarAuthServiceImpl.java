package com.baffang.user.service.impl;

import com.baffang.common.entity.CarAuth;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.enums.AuditTypeEnum;
import com.baffang.common.enums.entityEnum.AuthStatusEnum;
import com.baffang.common.exception.BusinessException;
import com.baffang.admin.service.AuditService;
import com.baffang.user.dto.CarAuthDTO;
import com.baffang.user.mapper.UserCarAuthMapper;
import com.baffang.user.service.CarAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 车辆认证服务实现类
 */
@Service
public class CarAuthServiceImpl implements CarAuthService {

    @Autowired
    private UserCarAuthMapper userCarAuthMapper;

    @Autowired
    private AuditService auditService;

    /**
     * 新增车辆认证
     *
     * @param userId     用户ID
     * @param carAuthDTO 车辆认证请求参数
     */
    @Override
    public void addCarAuth(Integer userId, CarAuthDTO carAuthDTO) {
        // 检查用户是否已有未删除的车辆认证记录
        int count = userCarAuthMapper.countCarAuthByUserId(userId);
        if (count > 0) {
            throw new BusinessException(ErrorMsg.CAR_AUTH_ALREADY_EXISTS);
        }

        // 创建CarAuth实体
        CarAuth carAuth = new CarAuth();

        // 复制DTO属性到实体
        BeanUtils.copyProperties(carAuthDTO, carAuth);

        // 设置用户ID
        carAuth.setOwnerId(userId);

        // 设置默认的认证状态为审核中
        carAuth.setAuthStatus(AuthStatusEnum.UNDER_REVIEW.getCode());

        // 设置删除状态为未删除
        carAuth.setIsDeleted(0);

        // 设置创建时间和更新时间
        Date now = new Date();
        carAuth.setCreateTime(now);
        carAuth.setUpdateTime(now);

        // 调用Mapper插入数据
        userCarAuthMapper.insertCarAuth(carAuth);

        // 创建审核记录
        String title = "车辆认证 - " + carAuthDTO.getVehicleNumber();
        String content = "车辆类型：" + carAuthDTO.getVehicleType() + "\n" +
                "电流类型：" + (carAuthDTO.getCurrentType() == 0 ? "直流电" : "交流电") + "\n" +
                "汽车品牌：" + carAuthDTO.getCarBrand() + "\n" +
                "汽车型号：" + carAuthDTO.getCarType() + "\n" +
                "最大充电电压：" + carAuthDTO.getMaxChargeVoltage() + "V\n" +
                "最大充电电流：" + carAuthDTO.getMaxChargeCurrent() + "A\n" +
                "最大充电功率：" + carAuthDTO.getMaxChargePower() + "W\n" +
                "电池容量：" + carAuthDTO.getBatteryCapacity() + "kWh\n" +
                "续航里程：" + carAuthDTO.getRange() + "km\n" +
                "车牌号：" + carAuthDTO.getVehicleNumber();

        auditService.createAuditRecord(
                AuditTypeEnum.CAR_AUTH.getCode(),
                carAuth.getId(),
                userId,
                title,
                content,
                null // 附件可以根据实际情况添加
        );
    }

    /**
     * 根据用户ID查询车辆认证信息
     *
     * @param userId 用户ID
     * @return 车辆认证信息
     */
    @Override
    public CarAuth getCarAuthByUserId(Integer userId) {
        // 调用Mapper查询数据
        CarAuth carAuth = userCarAuthMapper.getCarAuthByUserId(userId);

        // 检查是否存在车辆认证记录
        if (carAuth == null) {
            throw new BusinessException(ErrorMsg.CAR_AUTH_NOT_FOUND);
        }

        return carAuth;
    }

    /**
     * 删除车辆认证信息
     *
     * @param userId 用户ID
     */
    @Override
    public void deleteCarAuth(Integer userId) {
        // 1. 先检查车辆认证记录是否存在
        CarAuth carAuth = userCarAuthMapper.getCarAuthByUserId(userId);
        if (carAuth == null) {
            throw new BusinessException(ErrorMsg.CAR_AUTH_NOT_FOUND);
        }

        // 2. 调用Mapper执行软删除
        int affectedRows = userCarAuthMapper.deleteCarAuthByUserId(userId);

        // 3. 检查是否有记录被删除
        if (affectedRows == 0) {
            throw new BusinessException(ErrorMsg.CAR_AUTH_NOT_FOUND);
        }

        // 4. 更新审核记录状态为已取消
        auditService.withdraw(carAuth.getId(), userId);
    }

    /**
     * 更新车辆认证信息
     *
     * @param userId     用户ID
     * @param carAuthDTO 车辆认证更新参数
     */
    @Override
    public void updateCarAuth(Integer userId, CarAuthDTO carAuthDTO) {
        // 1. 先检查车辆认证记录是否存在
        CarAuth carAuth = userCarAuthMapper.getCarAuthByUserId(userId);
        if (carAuth == null) {
            throw new BusinessException(ErrorMsg.CAR_AUTH_NOT_FOUND);
        }

        // 2. 创建参数Map
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("carAuthDTO", carAuthDTO);

        // 3. 调用Mapper执行更新
        int affectedRows = userCarAuthMapper.updateCarAuthByUserId(params);

        // 4. 检查是否有记录被更新
        if (affectedRows == 0) {
            throw new BusinessException(ErrorMsg.CAR_AUTH_NOT_FOUND);
        }
    }
}