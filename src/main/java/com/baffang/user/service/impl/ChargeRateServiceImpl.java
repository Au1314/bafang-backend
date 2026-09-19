package com.baffang.user.service.impl;

import com.baffang.common.entity.ChargeRate;
import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.common.entity.PublicChargeStation;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.user.dto.ChargeRateDTO;
import com.baffang.user.mapper.UserChargeRateMapper;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.mapper.UserPublicChargeStationMapper;
import com.baffang.user.service.ChargeRateService;
import com.baffang.user.vo.ChargeRateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 充电桩评论服务实现类
 */
@Service
public class ChargeRateServiceImpl implements ChargeRateService {

    @Autowired
    private UserChargeRateMapper userChargeRateMapper;

    @Autowired
    private UserPrivateChargeStationMapper userPrivateChargeStationMapper;

    @Autowired
    private UserPublicChargeStationMapper userPublicChargeStationMapper;

    /**
     * 发布充电桩评论
     * 
     * @param userId        用户ID
     * @param chargeRateDTO 评论发布请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishChargeRate(Integer userId, ChargeRateDTO chargeRateDTO) {
        // 参数转换
        Integer stationId = Integer.valueOf(chargeRateDTO.getStationId());
        Integer stationType = Integer.valueOf(chargeRateDTO.getStationType());
        Integer score = Integer.valueOf(chargeRateDTO.getScore());
        Integer anonymous = Integer.valueOf(chargeRateDTO.getAnonymous());

        // 验证参数
        validateParams(stationType, score, anonymous);

        // 检查充电桩是否存在
        checkStationExists(stationId, stationType);

        // 保存评论
        ChargeRate chargeRate = new ChargeRate();
        chargeRate.setUserId(userId);
        chargeRate.setType(stationType);
        chargeRate.setChargingStationId(stationId);
        chargeRate.setAnonymous(anonymous);
        chargeRate.setScore(score);
        chargeRate.setComment(chargeRateDTO.getComment());
        chargeRate.setCreateTime(new Date());
        chargeRate.setUpdateTime(new Date());
        chargeRate.setIsDeleted(0);

        userChargeRateMapper.insertChargeRate(chargeRate);

        // 重新计算并更新平均评分
        updateAverageRating(stationId, stationType);
    }

    /**
     * 验证参数
     * 
     * @param stationType 桩类型
     * @param score       评分
     * @param anonymous   是否匿名
     */
    private void validateParams(Integer stationType, Integer score, Integer anonymous) {
        // 验证桩类型
        if (stationType != 0 && stationType != 1) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "无效的桩类型");
        }

        // 验证评分
        if (score < 1 || score > 5) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "评分必须在1-5分之间");
        }

        // 验证是否匿名
        if (anonymous != 0 && anonymous != 1) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "无效的匿名设置");
        }
    }

    /**
     * 检查充电桩是否存在
     * 
     * @param stationId   充电桩ID
     * @param stationType 桩类型
     */
    private void checkStationExists(Integer stationId, Integer stationType) {
        if (stationType == 1) {
            // 私桩
            PrivateChargeStation privateStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
            if (privateStation == null || privateStation.getIsDeleted() == 1) {
                throw new BusinessException(ErrorMsg.PRIVATE_STATION_NOT_EXIST.getCode(), "私桩不存在");
            }
        } else {
            // 公桩
            PublicChargeStation publicStation = userPublicChargeStationMapper.selectPublicStationById(stationId);
            if (publicStation == null) {
                throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "公桩不存在");
            }
        }
    }

    /**
     * 重新计算并更新平均评分
     * 
     * @param stationId   充电桩ID
     * @param stationType 桩类型
     */
    private void updateAverageRating(Integer stationId, Integer stationType) {
        // 计算平均评分
        Double averageRating = userChargeRateMapper.calculateAverageRating(stationId, stationType);

        if (stationType == 1) {
            // 更新私桩评分
            userPrivateChargeStationMapper.updatePrivateStationRating(stationId, averageRating);
        } else {
            // 更新公桩评分
            userPublicChargeStationMapper.updatePublicStationRating(stationId, averageRating);
        }
    }

    /**
     * 查询充电桩评论列表
     * 
     * @param stationId   充电桩ID
     * @param stationType 桩类型：0=公桩，1=私桩
     * @param pageNum     页码
     * @param pageSize    每页条数
     * @return 评论列表分页结果
     */
    @Override
    public PageResult<ChargeRateVO> queryChargeRateList(Integer stationId, Integer stationType, Integer pageNum,
            Integer pageSize) {
        // 检查充电桩是否存在
        checkStationExists(stationId, stationType);

        // 使用PageHelper进行分页
        com.github.pagehelper.PageHelper.startPage(pageNum, pageSize);

        // 查询评论列表
        List<ChargeRateVO> chargeRateVOList = userChargeRateMapper.selectChargeRateListByStationIdAndType(stationId,
                stationType);

        // 处理匿名用户的显示名称和头像
        for (ChargeRateVO chargeRateVO : chargeRateVOList) {
            if (chargeRateVO.getAnonymous() == 1) {
                chargeRateVO.setUsername("匿名用户");
                chargeRateVO.setAvatar(null);
            }
        }

        // 封装分页结果
        com.github.pagehelper.PageInfo<ChargeRateVO> pageInfo = new com.github.pagehelper.PageInfo<>(chargeRateVOList);
        return new PageResult<>(
                (int) pageInfo.getTotal(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getList());
    }

    /**
     * 删除充电桩评论
     * 
     * @param userId 用户ID
     * @param rateId 评论ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChargeRate(Integer userId, Integer rateId) {
        // 查询评论信息
        ChargeRate chargeRate = userChargeRateMapper.selectChargeRateById(rateId);

        // 验证评论是否存在
        if (chargeRate == null) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "评论不存在");
        }

        // 验证是否是用户自己的评论
        if (!chargeRate.getUserId().equals(userId)) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "只能删除自己发布的评论");
        }

        // 执行软删除
        int result = userChargeRateMapper.deleteChargeRate(rateId, userId);

        if (result <= 0) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "删除评论失败");
        }

        // 重新计算并更新平均评分
        updateAverageRating(chargeRate.getChargingStationId(), chargeRate.getType());
    }
}