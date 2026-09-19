package com.baffang.user.service.impl;

import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.common.entity.PrivateStationDynamicPrice;
import com.baffang.common.entity.PrivateStationReserve;
import com.baffang.common.entity.AuditRecord;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.enums.AuditTypeEnum;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.common.util.TimeUtil;
import com.baffang.user.dto.PrivateStationAuthDTO;
import com.baffang.user.dto.PrivateStationDynamicPriceDTO;
import com.baffang.user.dto.PrivateStationTimeConfigDTO;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.mapper.UserPrivateStationDynamicPriceMapper;
import com.baffang.user.mapper.UserPrivateStationReserveMapper;
import com.baffang.user.service.PrivateStationService;
import com.baffang.admin.service.AuditService;
import com.baffang.user.vo.HandlePrivateStationReserveVO;
import com.baffang.user.vo.PriceConfigVO;
import com.baffang.user.vo.PrivateStationListVO;
import com.baffang.user.vo.PrivateStationOwnerReserveListVO;
import com.baffang.user.vo.PrivateStationTimeConfigVO;
import com.baffang.user.vo.PrivateStationAuthProgressVO;
import com.baffang.user.vo.PrivateStationInfoVO;
import com.baffang.user.vo.ChargingPrivateStationInfoVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 私桩服务实现类
 */
@Service
public class PrivateStationServiceImpl implements PrivateStationService {

    @Autowired
    private UserPrivateChargeStationMapper userPrivateChargeStationMapper;

    @Autowired
    private UserPrivateStationReserveMapper privateStationReserveMapper;

    @Autowired
    private UserPrivateStationDynamicPriceMapper userPrivateStationDynamicPriceMapper;

    @Autowired
    private AuditService auditService;

    /**
     * 提交私桩认证信息
     *
     * @param userId                用户ID
     * @param privateStationAuthDTO 私桩认证请求参数
     */
    @Override
    public void submitPrivateStationAuth(Integer userId, PrivateStationAuthDTO privateStationAuthDTO) {
        // 1. 创建私桩信息实体
        PrivateChargeStation privateChargeStation = new PrivateChargeStation();

        // 2. 复制属性
        BeanUtils.copyProperties(privateStationAuthDTO, privateChargeStation);

        // 3. 手动转换Double类型为BigDecimal类型
        if (privateStationAuthDTO.getLongitude() != null) {
            privateChargeStation.setLongitude(BigDecimal.valueOf(privateStationAuthDTO.getLongitude()));
        }
        if (privateStationAuthDTO.getLatitude() != null) {
            privateChargeStation.setLatitude(BigDecimal.valueOf(privateStationAuthDTO.getLatitude()));
        }
        if (privateStationAuthDTO.getPrice() != null) {
            privateChargeStation.setPrice(BigDecimal.valueOf(privateStationAuthDTO.getPrice()));
        }

        // 4. 设置所有者ID
        privateChargeStation.setOwnerId(userId);

        // 5. 设置认证状态为"审核中"（1: 审核中）
        privateChargeStation.setAuthStatus(1);

        // 6. 设置初始状态
        privateChargeStation.setStatus(0); // 0: 空闲
        privateChargeStation.setOpenStatus(0); // 0: 关闭
        privateChargeStation.setCurrentVehicles(0); // 当前车辆数为0
        privateChargeStation.setIsDeleted(0); // 0: 正常

        // 7. 设置创建时间和更新时间
        Date now = new Date();
        privateChargeStation.setCreateTime(now);
        privateChargeStation.setUpdateTime(now);

        // 8. 调用Mapper插入私桩信息
        userPrivateChargeStationMapper.insert(privateChargeStation);

        // 9. 创建审核记录
        String title = "私桩认证 - " + privateStationAuthDTO.getName();
        String content = "私桩名称：" + privateStationAuthDTO.getName() + "\n" +
                "地点名称：" + privateStationAuthDTO.getLocationName() + "\n" +
                "经度：" + privateStationAuthDTO.getLongitude() + "\n" +
                "纬度：" + privateStationAuthDTO.getLatitude() + "\n" +
                "价格：" + privateStationAuthDTO.getPrice() + "元/度\n" +
                "私桩类型：" + (privateStationAuthDTO.getType() == 0 ? "普通私桩" : "专业私桩") + "\n" +
                "电流类型：" + (privateStationAuthDTO.getCurrentType() == 0 ? "直流电" : "交流电") + "\n" +
                "最大充电电压：" + privateStationAuthDTO.getMaxChargeVoltage() + "V\n" +
                "最大充电电流：" + privateStationAuthDTO.getMaxChargeCurrent() + "A\n" +
                "最大充电功率：" + privateStationAuthDTO.getMaxChargePower() + "W\n" +
                "最大车辆数：" + privateStationAuthDTO.getMaxVehicles() + "\n" +
                "描述：" + (privateStationAuthDTO.getDescription() != null ? privateStationAuthDTO.getDescription() : "无");

        auditService.createAuditRecord(
                AuditTypeEnum.STATION_AUTH.getCode(),
                privateChargeStation.getId(),
                userId,
                title,
                content,
                privateStationAuthDTO.getImage());
    }

    /**
     * 根据用户ID查询私桩列表（分页）
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 私桩列表分页结果
     */
    @Override
    public PageResult<PrivateStationListVO> getPrivateStationListByUserId(Integer userId, Integer pageNum,
            Integer pageSize) {
        // 1. 设置默认分页参数
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        // 2. 开始分页
        PageHelper.startPage(pageNum, pageSize);

        // 3. 调用Mapper查询私桩列表
        java.util.List<PrivateStationListVO> privateStationList = userPrivateChargeStationMapper
                .selectPrivateStationListByOwnerId(userId);

        // 4. 创建PageInfo对象
        PageInfo<PrivateStationListVO> pageInfo = new PageInfo<>(privateStationList);

        // 5. 封装PageResult对象并返回
        return new PageResult<>((int) pageInfo.getTotal(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getList());
    }

    /**
     * 解绑私桩
     *
     * @param userId    用户ID
     * @param stationId 私桩ID
     */
    @Override
    public void unbindPrivateStation(Integer userId, Integer stationId) {
        // 调用Mapper解绑私桩
        int rows = userPrivateChargeStationMapper.unbindPrivateStation(stationId, userId);

        // 检查是否解绑成功
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 更新审核记录状态为已取消
        AuditRecord auditRecord = auditService.getByBusinessIdAndType(
                stationId, AuditTypeEnum.STATION_AUTH.getCode());

        if (auditRecord != null) {
            auditService.withdraw(auditRecord.getId(), userId);
        }
    }

    /**
     * 设置私桩固定价格
     *
     * @param userId    用户ID
     * @param stationId 私桩ID
     * @param price     固定价格（元/度）
     */
    @Override
    public void setPrivateStationFixedPrice(Integer userId, Integer stationId, Double price) {
        // 调用Mapper更新私桩价格
        int rows = userPrivateChargeStationMapper.updatePrivateStationPrice(stationId, userId, price);

        // 检查是否更新成功
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
    }

    /**
     * 查询私桩时间配置
     *
     * @param userId    用户ID
     * @param stationId 私桩ID
     * @return 私桩时间配置信息
     */
    @Override
    public PrivateStationTimeConfigVO getPrivateStationTimeConfig(Integer userId, Integer stationId) {
        // 调用Mapper查询私桩时间配置
        PrivateStationTimeConfigVO timeConfig = userPrivateChargeStationMapper
                .selectPrivateStationTimeConfigByOwnerIdAndStationId(userId, stationId);

        // 检查是否查询到结果
        if (timeConfig == null) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 将数据库DATETIME格式转换为时间字符串
        if (timeConfig.getOpenTime() != null) {
            timeConfig.setOpenTime(TimeUtil.datetimeStrToTimeStr(timeConfig.getOpenTime()));
        }
        if (timeConfig.getCloseTime() != null) {
            timeConfig.setCloseTime(TimeUtil.datetimeStrToTimeStr(timeConfig.getCloseTime()));
        }

        return timeConfig;
    }

    /**
     * 设置私桩启停时间
     *
     * @param userId        用户ID
     * @param stationId     私桩ID
     * @param timeConfigDTO 时间配置参数
     */
    @Override
    public void setPrivateStationTimeConfig(Integer userId, Integer stationId,
            PrivateStationTimeConfigDTO timeConfigDTO) {
        // 将时间字符串转换为数据库可接受的DATETIME格式
        String openTimeStr = TimeUtil.timeStrToCurrentDatetimeStr(timeConfigDTO.getOpenTime());
        String closeTimeStr = TimeUtil.timeStrToCurrentDatetimeStr(timeConfigDTO.getCloseTime());

        // 调用Mapper更新私桩启停时间
        int rows = userPrivateChargeStationMapper.updatePrivateStationTimeConfig(stationId, userId,
                openTimeStr, closeTimeStr);

        // 检查是否更新成功
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
    }

    /**
     * 设置私桩状态（手动开关）
     *
     * @param userId     用户ID
     * @param stationId  私桩ID
     * @param openStatus 开关状态，0=关闭，1=开启
     */
    @Override
    public void setPrivateStationStatus(Integer userId, Integer stationId, Integer openStatus) {
        // 调用Mapper更新私桩状态
        int rows = userPrivateChargeStationMapper.updatePrivateStationStatus(stationId, userId, openStatus);

        // 检查是否更新成功
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
    }

    /**
     * 设置私桩动态价格
     *
     * @param userId                        用户ID
     * @param stationId                     私桩ID
     * @param privateStationDynamicPriceDTO 私桩动态价格设置请求参数
     */
    @Override
    public void setPrivateStationDynamicPrice(Integer userId, Integer stationId,
            PrivateStationDynamicPriceDTO privateStationDynamicPriceDTO) {
        // 1. 验证私桩是否存在且用户是所有者
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
        if (!privateChargeStation.getOwnerId().equals(userId)) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 2. 获取动态价格规则列表
        List<PrivateStationDynamicPriceDTO.DynamicPriceRuleDTO> rules = privateStationDynamicPriceDTO.getRules();
        if (rules == null || rules.isEmpty()) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        // 3. 检查时间重叠
        checkTimeOverlap(rules);

        // 4. 转换时间格式并准备插入数据
        List<PrivateStationDynamicPrice> dynamicPrices = new ArrayList<>();
        Date now = new Date();

        for (PrivateStationDynamicPriceDTO.DynamicPriceRuleDTO rule : rules) {
            PrivateStationDynamicPrice dynamicPrice = new PrivateStationDynamicPrice();
            dynamicPrice.setStationId(stationId);
            dynamicPrice.setPrice(new BigDecimal(rule.getPrice()));
            dynamicPrice.setIsDeleted(0);
            dynamicPrice.setCreateTime(now);

            // 处理时间格式转换
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date beginTime = sdf.parse(rule.getBeginTime());
                Date endTime = sdf.parse(rule.getEndTime());
                dynamicPrice.setBeginTime(beginTime);
                dynamicPrice.setEndTime(endTime);
            } catch (ParseException e) {
                throw new BusinessException(ErrorMsg.PARAM_ERROR);
            }

            dynamicPrices.add(dynamicPrice);
        }

        // 5. 删除该私桩现有的所有动态价格规则（逻辑删除）
        userPrivateStationDynamicPriceMapper.deleteDynamicPriceByStationId(stationId);

        // 6. 批量插入新的动态价格规则
        if (!dynamicPrices.isEmpty()) {
            userPrivateStationDynamicPriceMapper.batchInsertDynamicPrice(dynamicPrices);
        }

        // 7. 更新私桩的定价类型为动态价格
        userPrivateChargeStationMapper.updatePriceType(stationId, userId, 1);
    }

    /**
     * 开启/关闭私桩动态价格
     *
     * @param userId    用户ID
     * @param stationId 私桩ID
     * @param priceType 价格类型：0-静态价格，1-动态价格
     */
    @Override
    public void setPrivateStationPriceType(Integer userId, Integer stationId, Integer priceType) {
        // 1. 验证priceType参数是否合法
        if (priceType != 0 && priceType != 1) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }

        // 2. 验证私桩是否存在且用户是所有者
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
        if (!privateChargeStation.getOwnerId().equals(userId)) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 3. 调用Mapper方法更新私桩的价格类型
        int rows = userPrivateChargeStationMapper.updatePriceType(stationId, userId, priceType);
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
    }

    /**
     * 查询私桩价格配置
     *
     * @param userId    用户ID
     * @param stationId 私桩ID
     * @return 价格配置信息
     */
    @Override
    public PriceConfigVO getPrivateStationPriceConfig(Integer userId, Integer stationId) {
        // 1. 验证私桩是否存在且用户是所有者
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
        if (!privateChargeStation.getOwnerId().equals(userId)) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 2. 创建PriceConfigVO对象并设置基本信息
        PriceConfigVO priceConfigVO = new PriceConfigVO();
        priceConfigVO.setStationId(stationId);
        priceConfigVO.setPriceType(privateChargeStation.getPriceType());
        priceConfigVO.setFixedPrice(
                privateChargeStation.getPrice() != null ? privateChargeStation.getPrice().doubleValue() : 0.0);

        // 3. 查询动态价格规则
        List<PrivateStationDynamicPrice> dynamicPrices = userPrivateStationDynamicPriceMapper
                .selectDynamicPriceByStationId(stationId);
        List<PriceConfigVO.DynamicRuleVO> dynamicRules = new ArrayList<>();

        // 4. 转换动态价格规则格式
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        for (PrivateStationDynamicPrice dynamicPrice : dynamicPrices) {
            PriceConfigVO.DynamicRuleVO dynamicRuleVO = new PriceConfigVO.DynamicRuleVO();
            dynamicRuleVO.setRuleId(dynamicPrice.getId());
            dynamicRuleVO.setStartTime(sdf.format(dynamicPrice.getBeginTime()));
            dynamicRuleVO.setEndTime(sdf.format(dynamicPrice.getEndTime()));
            dynamicRuleVO.setPrice(dynamicPrice.getPrice().doubleValue());
            dynamicRules.add(dynamicRuleVO);
        }

        // 5. 设置动态价格规则列表
        priceConfigVO.setDynamicRules(dynamicRules);

        return priceConfigVO;
    }

    @Override
    public void deletePrivateStationDynamicPriceRule(Integer userId, Integer stationId, Integer ruleId) {
        // 1. 验证私桩是否存在且用户是所有者
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
        if (!privateChargeStation.getOwnerId().equals(userId)) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 2. 调用Mapper删除动态价格规则
        int rows = userPrivateStationDynamicPriceMapper.deleteDynamicPriceRule(ruleId, stationId);
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.DYNAMIC_PRICE_RULE_NOT_FOUND);
        }
    }

    /**
     * 检查动态价格规则是否有时间重叠
     *
     * @param rules 动态价格规则列表
     */
    private void checkTimeOverlap(List<PrivateStationDynamicPriceDTO.DynamicPriceRuleDTO> rules) {
        try {
            for (int i = 0; i < rules.size(); i++) {
                int[] timeRange1 = convertTimeToMinutes(rules.get(i).getBeginTime(), rules.get(i).getEndTime());
                int beginMin1 = timeRange1[0];
                int endMin1 = timeRange1[1];

                for (int j = i + 1; j < rules.size(); j++) {
                    int[] timeRange2 = convertTimeToMinutes(rules.get(j).getBeginTime(), rules.get(j).getEndTime());
                    int beginMin2 = timeRange2[0];
                    int endMin2 = timeRange2[1];

                    // 检查时间重叠
                    if (isOverlapping(beginMin1, endMin1, beginMin2, endMin2)) {
                        throw new BusinessException(ErrorMsg.DYNAMIC_PRICE_TIME_OVERLAP);
                    }
                }
            }
        } catch (ParseException e) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR);
        }
    }

    /**
     * 将时间字符串转换为分钟数
     *
     * @param beginTime 开始时间字符串 (HH:mm:ss)
     * @param endTime   结束时间字符串 (HH:mm:ss)
     * @return 包含开始分钟数和结束分钟数的数组
     */
    private int[] convertTimeToMinutes(String beginTime, String endTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date begin = sdf.parse(beginTime);
        Date end = sdf.parse(endTime);

        int beginMin = begin.getHours() * 60 + begin.getMinutes();
        int endMin = end.getHours() * 60 + end.getMinutes();

        // 如果结束时间早于开始时间，说明是跨天时间范围，结束时间加上1440分钟(24小时)
        if (endMin < beginMin) {
            endMin += 1440;
        }

        return new int[] { beginMin, endMin };
    }

    /**
     * 检查两个时间范围是否重叠
     *
     * @param beginMin1 第一个时间范围的开始分钟数
     * @param endMin1   第一个时间范围的结束分钟数
     * @param beginMin2 第二个时间范围的开始分钟数
     * @param endMin2   第二个时间范围的结束分钟数
     * @return 如果重叠返回true，否则返回false
     */
    private boolean isOverlapping(int beginMin1, int endMin1, int beginMin2, int endMin2) {
        // 确保时间范围是规范的
        if (endMin2 < beginMin2) {
            endMin2 += 1440;
        }

        // 调整第一个时间范围，检查与第二个时间范围的重叠
        int tempBegin = beginMin1;
        int tempEnd = endMin1;

        // 如果第一个时间范围不跨天，检查是否与第二个时间范围重叠
        if (tempEnd < tempBegin) {
            tempEnd += 1440;
        }

        // 检查两个时间范围是否重叠
        return !(tempEnd <= beginMin2 || tempBegin >= endMin2);
    }

    @Override
    public PageResult<PrivateStationOwnerReserveListVO> queryPrivateStationReserve(Integer userId, Integer status,
            Integer pageNum, Integer pageSize) {
        // 1. 设置默认分页参数
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        // 2. 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);

        // 3. 调用Mapper方法查询预约记录
        List<PrivateStationOwnerReserveListVO> list = privateStationReserveMapper
                .queryPrivateStationReserveByOwner(userId, status);

        // 4. 封装分页结果
        PageInfo<PrivateStationOwnerReserveListVO> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                (int) pageInfo.getTotal(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                list);
    }

    @Override
    public com.baffang.user.vo.HandlePrivateStationReserveVO handlePrivateStationReserve(Integer userId,
            Integer reserveId, Integer status) {
        // 1. 调用Mapper方法处理预约申请
        int rows = privateStationReserveMapper.handlePrivateStationReserve(userId, reserveId, status);

        // 2. 检查是否处理成功
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.RESERVE_NOT_FOUND_OR_CANNOT_CANCEL);
        }

        // 3. 查询更新后的预约记录
        PrivateStationReserve reserve = privateStationReserveMapper.selectPrivateStationReserveById(reserveId);

        // 4. 封装结果返回
        HandlePrivateStationReserveVO vo = new HandlePrivateStationReserveVO();
        vo.setReserveId(reserveId);
        vo.setStatus(reserve.getStatus());
        // 格式化时间为字符串
        vo.setUpdateTime(TimeUtil.formatDatetime(reserve.getUpdateTime()));

        return vo;
    }

    @Override
    public void resubmitPrivateStationAuth(Integer userId, Integer stationId,
            PrivateStationAuthDTO privateStationAuthDTO) {
        // 1. 检查私桩是否存在且未被删除
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 2. 检查是否为私桩所有者
        if (!userId.equals(privateChargeStation.getOwnerId())) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 3. 更新私桩信息
        int rows = userPrivateChargeStationMapper.updatePrivateStationInfo(stationId, userId, privateStationAuthDTO);
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }
    }

    @Override
    public PrivateStationAuthProgressVO getPrivateStationAuthProgress(Integer userId, Integer stationId) {
        // 1. 检查私桩是否存在且未被删除
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 2. 检查是否为私桩所有者
        if (!userId.equals(privateChargeStation.getOwnerId())) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 3. 获取审核记录
        AuditRecord auditRecord = auditService.getByBusinessIdAndType(
                stationId, AuditTypeEnum.STATION_AUTH.getCode());

        // 4. 创建并填充VO对象
        PrivateStationAuthProgressVO vo = new PrivateStationAuthProgressVO();
        vo.setStationId(stationId);
        vo.setAuthStatus(privateChargeStation.getAuthStatus());

        if (auditRecord != null) {
            vo.setId(auditRecord.getId());
            vo.setAuditNo(auditRecord.getAuditNo());
            vo.setAuditType(auditRecord.getAuditType());
            vo.setBusinessId(auditRecord.getBusinessId());
            vo.setApplicantId(auditRecord.getApplicantId());
            vo.setTitle(auditRecord.getTitle());
            vo.setContent(auditRecord.getContent());
            vo.setAttachments(auditRecord.getAttachments());
            vo.setCurrentStatus(auditRecord.getCurrentStatus());
            vo.setCurrentAdminId(auditRecord.getCurrentAdminId());
            vo.setPriority(auditRecord.getPriority());
            vo.setSubmitTime(auditRecord.getSubmitTime());
            vo.setFinishTime(auditRecord.getFinishTime());
            vo.setFinalOpinion(auditRecord.getFinalOpinion());
            vo.setRejectReason(auditRecord.getRejectReason());
            vo.setCreateTime(auditRecord.getCreateTime());
            vo.setUpdateTime(auditRecord.getUpdateTime());
        }

        return vo;
    }

    @Override
    public PrivateStationInfoVO getPrivateStationInfo(Integer userId, Integer stationId) {
        // 1. 验证私桩是否存在且未被删除
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 2. 验证用户是否为私桩所有者
        if (!userId.equals(privateChargeStation.getOwnerId())) {
            throw new BusinessException(ErrorMsg.FORBIDDEN);
        }

        // 3. 创建并填充PrivateStationInfoVO对象
        PrivateStationInfoVO privateStationInfoVO = new PrivateStationInfoVO();
        // 使用BeanUtils复制所有字段（除了is_deleted，VO中没有该字段）
        BeanUtils.copyProperties(privateChargeStation, privateStationInfoVO);

        // 4. 查询动态价格规则
        List<PrivateStationDynamicPrice> dynamicPrices = userPrivateStationDynamicPriceMapper
                .selectDynamicPriceByStationId(stationId);
        List<PriceConfigVO.DynamicRuleVO> dynamicRules = new ArrayList<>();

        // 5. 转换动态价格规则格式
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        for (PrivateStationDynamicPrice dynamicPrice : dynamicPrices) {
            PriceConfigVO.DynamicRuleVO dynamicRuleVO = new PriceConfigVO.DynamicRuleVO();
            dynamicRuleVO.setRuleId(dynamicPrice.getId());
            dynamicRuleVO.setStartTime(sdf.format(dynamicPrice.getBeginTime()));
            dynamicRuleVO.setEndTime(sdf.format(dynamicPrice.getEndTime()));
            dynamicRuleVO.setPrice(dynamicPrice.getPrice().doubleValue());
            dynamicRules.add(dynamicRuleVO);
        }

        // 6. 设置动态价格规则列表
        privateStationInfoVO.setDynamicRules(dynamicRules);

        return privateStationInfoVO;
    }

    @Override
    public ChargingPrivateStationInfoVO getChargingPrivateStationInfo(Integer stationId) {
        // 1. 验证私桩是否存在且未被删除
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null || privateChargeStation.getIsDeleted() == 1) {
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 2. 创建并填充ChargingPrivateStationInfoVO对象
        ChargingPrivateStationInfoVO chargingPrivateStationInfoVO = new ChargingPrivateStationInfoVO();
        // 使用BeanUtils复制所有字段（除了is_deleted和管理相关字段，VO中没有这些字段）
        BeanUtils.copyProperties(privateChargeStation, chargingPrivateStationInfoVO);

        // 3. 查询动态价格规则
        List<PrivateStationDynamicPrice> dynamicPrices = userPrivateStationDynamicPriceMapper
                .selectDynamicPriceByStationId(stationId);
        List<PriceConfigVO.DynamicRuleVO> dynamicRules = new ArrayList<>();

        // 4. 转换动态价格规则格式
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        for (PrivateStationDynamicPrice dynamicPrice : dynamicPrices) {
            PriceConfigVO.DynamicRuleVO dynamicRuleVO = new PriceConfigVO.DynamicRuleVO();
            dynamicRuleVO.setRuleId(dynamicPrice.getId());
            dynamicRuleVO.setStartTime(sdf.format(dynamicPrice.getBeginTime()));
            dynamicRuleVO.setEndTime(sdf.format(dynamicPrice.getEndTime()));
            dynamicRuleVO.setPrice(dynamicPrice.getPrice().doubleValue());
            dynamicRules.add(dynamicRuleVO);
        }

        // 5. 设置动态价格规则列表
        chargingPrivateStationInfoVO.setDynamicRules(dynamicRules);

        return chargingPrivateStationInfoVO;
    }
}
