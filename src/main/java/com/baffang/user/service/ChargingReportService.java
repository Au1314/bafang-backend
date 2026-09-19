package com.baffang.user.service;

import com.baffang.common.result.PageResult;
import com.baffang.user.dto.ChargingReportDTO;
import com.baffang.user.vo.ChargingReportDetailVO;
import com.baffang.user.vo.ChargingReportListVO;
import com.baffang.user.vo.ChargingReportVO;

/**
 * 充电桩故障/举报服务接口
 */
public interface ChargingReportService {

    /**
     * 提交充电桩故障/举报
     * 
     * @param reportDTO 举报请求参数
     * @param userId    用户ID
     * @return 举报结果
     */
    ChargingReportVO submitReport(ChargingReportDTO reportDTO, Integer userId);

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
    PageResult<ChargingReportListVO> getMyReportList(
            Integer userId,
            Integer reportType,
            Integer status,
            Integer pageNum,
            Integer pageSize);

    /**
     * 查询充电桩故障/举报详细信息
     * 
     * @param reportId 上报记录ID
     * @param userId   用户ID
     * @return 上报详细信息
     */
    ChargingReportDetailVO getReportDetail(Integer reportId, Integer userId);

    /**
     * 取消上报
     * 
     * @param reportId 上报记录ID
     * @param userId   用户ID
     */
    void cancelReport(Integer reportId, Integer userId);
}