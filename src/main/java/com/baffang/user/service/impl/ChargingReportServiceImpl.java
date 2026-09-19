package com.baffang.user.service.impl;

import com.baffang.common.entity.AuditRecord;
import com.baffang.common.entity.PrivateChargeStation;
import com.baffang.common.enums.AuditStatusEnum;
import com.baffang.common.enums.AuditTypeEnum;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.user.dto.ChargingReportDTO;
import com.baffang.user.mapper.UserAuditRecordMapper;
import com.baffang.user.mapper.UserPrivateChargeStationMapper;
import com.baffang.user.service.ChargingReportService;
import com.baffang.user.vo.ChargingReportDetailVO;
import com.baffang.user.vo.ChargingReportListVO;
import com.baffang.user.vo.ChargingReportVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 充电桩故障/举报服务实现类
 */
@Service
public class ChargingReportServiceImpl implements ChargingReportService {

    @Autowired
    private UserAuditRecordMapper userAuditRecordMapper;

    @Autowired
    private UserPrivateChargeStationMapper userPrivateChargeStationMapper;

    // 用于生成唯一流水号的计数器
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    /**
     * 提交充电桩故障/举报
     *
     * @param reportDTO 举报请求参数
     * @param userId    用户ID
     * @return 举报结果
     */
    @Override
    public ChargingReportVO submitReport(ChargingReportDTO reportDTO, Integer userId) {
        // 1. 验证充电桩是否存在
        Integer stationId = reportDTO.getStationId();
        PrivateChargeStation privateChargeStation = userPrivateChargeStationMapper.selectPrivateStationById(stationId);
        if (privateChargeStation == null) {
            // 抛出充电桩不存在的异常
            throw new BusinessException(ErrorMsg.STATION_NOT_FOUND);
        }

        // 2. 创建审核记录实体
        AuditRecord auditRecord = new AuditRecord();

        // 设置审核单号
        String auditNo = generateAuditNo();
        auditRecord.setAuditNo(auditNo);

        // 根据reportType设置审核类型
        // reportType: 0=举报/投诉 -> auditType: 2=充电桩投诉
        // reportType: 1=故障 -> auditType: 3=充电桩故障
        Integer auditType = reportDTO.getReportType() == 0 ? AuditTypeEnum.STATION_COMPLAINT.getCode()
                : AuditTypeEnum.STATION_FAULT.getCode();
        auditRecord.setAuditType(auditType);

        // 设置业务ID（充电桩ID）
        auditRecord.setBusinessId(reportDTO.getStationId());

        // 设置申请人ID
        auditRecord.setApplicantId(userId);

        // 设置标题和内容
        auditRecord.setTitle(reportDTO.getTitle());
        auditRecord.setContent(reportDTO.getContent());

        // 设置附件（图片URL）
        auditRecord.setAttachments(reportDTO.getImages());

        // 设置初始状态为待审核
        auditRecord.setCurrentStatus(AuditStatusEnum.PENDING.getCode());

        // 设置优先级为普通
        auditRecord.setPriority(1);

        // 插入数据库
        userAuditRecordMapper.insertAuditRecord(auditRecord);

        // 构造返回结果
        ChargingReportVO reportVO = new ChargingReportVO();
        reportVO.setReportId(auditRecord.getId());
        reportVO.setReportNo(auditNo);
        reportVO.setStatus(AuditStatusEnum.PENDING.getCode());

        return reportVO;
    }

    /**
     * 查询用户的充电桩故障/举报记录列表
     *
     * @param userId     用户ID
     * @param reportType 上报类型：0=举报，1=故障
     * @param status     处理状态：0=待处理，1=处理中，2=已处理，3=已关闭
     * @param pageNum    页码，默认1
     * @param pageSize   每页条数，默认10
     * @return 分页后的上报记录列表
     */
    @Override
    public PageResult<ChargingReportListVO> getMyReportList(
            Integer userId,
            Integer reportType,
            Integer status,
            Integer pageNum,
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

        // 3. 调用Mapper查询上报记录列表
        java.util.List<ChargingReportListVO> reportList = userAuditRecordMapper
                .selectMyReportList(userId, reportType, status);

        // 4. 创建PageInfo对象
        PageInfo<ChargingReportListVO> pageInfo = new PageInfo<>(reportList);

        // 5. 封装PageResult对象并返回
        return new PageResult<>((int) pageInfo.getTotal(),
                pageInfo.getPages(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getList());
    }

    /**
     * 查询充电桩故障/举报详细信息
     *
     * @param reportId 上报记录ID
     * @param userId   用户ID
     * @return 上报详细信息
     */
    @Override
    public ChargingReportDetailVO getReportDetail(Integer reportId, Integer userId) {
        // 1. 根据reportId查询上报详细信息
        ChargingReportDetailVO reportDetail = userAuditRecordMapper.selectReportDetailById(reportId);

        // 2. 验证上报记录是否存在
        if (reportDetail == null) {
            throw new BusinessException(ErrorMsg.DATA_NOT_FOUND);
        }

        // 3. 验证该上报记录是否属于当前用户
        if (!reportDetail.getUserId().equals(userId)) {
            throw new BusinessException(ErrorMsg.UNAUTHORIZED);
        }

        // 4. 返回上报详细信息
        return reportDetail;
    }

    /**
     * 取消上报
     *
     * @param reportId 上报记录ID
     * @param userId   用户ID
     */
    @Override
    public void cancelReport(Integer reportId, Integer userId) {
        // 1. 获取上报记录的当前状态
        Integer currentStatus = userAuditRecordMapper.getReportStatus(reportId, userId);

        // 2. 验证上报记录是否存在
        if (currentStatus == null) {
            throw new BusinessException(ErrorMsg.DATA_NOT_FOUND);
        }

        // 3. 验证是否可以取消（只有状态为0：待审核和4：需补充材料的记录可取消）
        if (currentStatus != 0 && currentStatus != 4) {
            throw new BusinessException(ErrorMsg.AUDIT_CANNOT_WITHDRAW);
        }

        // 4. 执行取消操作（将状态更新为已撤回）
        int rows = userAuditRecordMapper.cancelReport(reportId, userId);

        // 5. 验证取消操作是否成功
        if (rows == 0) {
            throw new BusinessException(ErrorMsg.UPDATE_FAILED);
        }
    }

    /**
     * 生成唯一的审核单号
     * 格式：REP + 年月日 + 3位流水号
     * 例如：REP20260107001
     *
     * @return 审核单号
     */
    private String generateAuditNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());

        // 获取3位流水号，循环使用001-999
        int counter = COUNTER.incrementAndGet();
        if (counter > 999) {
            COUNTER.set(1);
            counter = 1;
        }

        return "REP" + dateStr + String.format("%03d", counter);
    }
}