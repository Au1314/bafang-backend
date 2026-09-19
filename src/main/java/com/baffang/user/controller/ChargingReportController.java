package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.ChargingReportDTO;
import com.baffang.user.service.ChargingReportService;
import com.baffang.user.vo.ChargingReportListVO;
import com.baffang.user.vo.ChargingReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 充电桩故障/举报控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class ChargingReportController {

    @Autowired
    private ChargingReportService chargingReportService;

    /**
     * 提交充电桩故障/举报接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param reportDTO     举报请求参数
     * @return 提交结果
     */
    @PostMapping("/charging/report")
    public Result<ChargingReportVO> submitChargingReport(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody ChargingReportDTO reportDTO) {
        // 1. 从Authorization头中获取令牌（如果存在）
        Integer userId = 1; // 临时设置默认值，实际项目中需要从令牌中获取
        if (authorization != null && !authorization.isEmpty()) {
            // 去掉Bearer前缀
            String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
            // 解析令牌获取用户ID
            userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        }

        // 2. 调用Service层方法提交故障/举报
        ChargingReportVO reportVO = chargingReportService.submitReport(reportDTO, userId);

        // 3. 返回结果
        return Result.success(reportVO, "上报提交成功，我们会尽快处理");
    }

    /**
     * 查询用户的充电桩故障/举报记录列表接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param reportType    上报类型：0=举报，1=故障
     * @param status        处理状态：0=待处理，1=处理中，2=已处理，3=已关闭
     * @param pageNum       页码，默认1
     * @param pageSize      每页条数，默认10
     * @return 查询结果
     */
    @GetMapping("/charging/report/my")
    public Result<?> getMyReportList(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "reportType", required = false) Integer reportType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        // 1. 从Authorization头中获取令牌（如果存在）
        Integer userId = 1; // 临时设置默认值，实际项目中需要从令牌中获取
        if (authorization != null && !authorization.isEmpty()) {
            // 去掉Bearer前缀
            String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
            // 解析令牌获取用户ID
            userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        }

        // 2. 调用Service层方法查询上报记录列表
        var reportList = chargingReportService.getMyReportList(userId, reportType, status, pageNum, pageSize);

        // 3. 返回结果
        return Result.success(reportList, "查询成功");
    }

    /**
     * 查询充电桩故障/举报详细信息接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param reportId      上报记录ID
     * @return 查询结果
     */
    @GetMapping("/charging/report/{reportId}/info")
    public Result<?> getReportDetail(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("reportId") Integer reportId) {
        // 1. 从Authorization头中获取令牌
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        // 解析令牌获取用户ID
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 2. 调用Service层方法查询上报详细信息
        var reportDetail = chargingReportService.getReportDetail(reportId, userId);

        // 3. 返回结果
        return Result.success(reportDetail, "查询成功");
    }

    /**
     * 取消上报接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param reportId      上报记录ID
     * @return 取消结果
     */
    @DeleteMapping("/charging/report/{reportId}")
    public Result<?> cancelReport(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("reportId") Integer reportId) {
        // 1. 从Authorization头中获取令牌
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        // 解析令牌获取用户ID
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 2. 调用Service层方法取消上报
        chargingReportService.cancelReport(reportId, userId);

        // 3. 返回结果
        return Result.success(null, "已成功取消");
    }
}