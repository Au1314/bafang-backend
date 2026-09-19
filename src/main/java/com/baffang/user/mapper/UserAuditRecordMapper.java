package com.baffang.user.mapper;

import com.baffang.common.entity.AuditRecord;
import com.baffang.user.vo.ChargingReportDetailVO;
import com.baffang.user.vo.ChargingReportListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户端审核记录Mapper
 */
@Mapper
public interface UserAuditRecordMapper {

    /**
     * 插入审核记录（用于充电桩故障/举报）
     * 
     * @param auditRecord 审核记录实体
     * @return 影响行数
     */
    int insertAuditRecord(AuditRecord auditRecord);

    /**
     * 查询用户的充电桩故障/举报记录列表
     * 
     * @param applicantId 用户ID
     * @param reportType  上报类型：0=举报，1=故障
     * @param status      处理状态：0=待处理，1=处理中，2=已处理，3=已关闭
     * @return 上报记录列表
     */
    List<ChargingReportListVO> selectMyReportList(
            @Param("applicantId") Integer applicantId,
            @Param("reportType") Integer reportType,
            @Param("status") Integer status);

    /**
     * 查询充电桩故障/举报详细信息
     * 
     * @param reportId 上报记录ID
     * @return 上报详细信息
     */
    ChargingReportDetailVO selectReportDetailById(@Param("reportId") Integer reportId);

    /**
     * 获取上报记录的当前状态
     * 
     * @param reportId 上报记录ID
     * @param userId   用户ID
     * @return 当前状态
     */
    Integer getReportStatus(
            @Param("reportId") Integer reportId,
            @Param("userId") Integer userId);

    /**
     * 取消上报（将状态更新为已撤回）
     * 
     * @param reportId 上报记录ID
     * @param userId   用户ID
     * @return 影响行数
     */
    int cancelReport(
            @Param("reportId") Integer reportId,
            @Param("userId") Integer userId);
}