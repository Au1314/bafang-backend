package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.PrivateStationReserveDTO;
import com.baffang.user.service.PrivateStationUseService;
import com.baffang.user.vo.PrivateStationReserveListVO;
import com.baffang.user.vo.PrivateStationReserveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 私桩使用控制器
 */
@RestController
@RequestMapping("/api/v1/user/charging")
public class StationUseController {

    @Autowired
    private PrivateStationUseService privateStationUseService;

    /**
     * 提交私桩预约申请
     *
     * @param authorization            请求头中的Authorization令牌
     * @param stationId                私桩ID
     * @param privateStationReserveDTO 预约请求参数
     * @return 预约结果
     */
    @PostMapping("/private-station/{stationId}/reserve")
    public Result<?> reservePrivateStation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("stationId") Integer stationId,
            @RequestBody PrivateStationReserveDTO privateStationReserveDTO) {
        // 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 调用Service层方法提交私桩预约申请
        PrivateStationReserveVO reserveResult = privateStationUseService.reservePrivateStation(userId, stationId,
                privateStationReserveDTO);

        // 返回预约结果
        Result<PrivateStationReserveVO> result = Result.success(reserveResult);
        result.setMsg("预约申请提交成功，等待桩主确认");
        return result;
    }

    /**
     * 取消私桩预约
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @return 取消结果
     */
    @DeleteMapping("/private-station/{stationId}/reserve")
    public Result<?> cancelPrivateStationReserve(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId) {
        // 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 调用Service层方法取消私桩预约
        privateStationUseService.cancelPrivateStationReserve(userId, stationId);

        // 返回取消结果
        Result<?> result = Result.success();
        result.setMsg("预约已取消");
        return result;
    }

    /**
     * 查询个人私桩预约记录
     *
     * @param authorization 请求头中的Authorization令牌
     * @param status        预约状态（可选，0=待确认，1=已确认，2=已取消）
     * @param pageNum       页码（可选，默认1）
     * @param pageSize      每页条数（可选，默认10）
     * @return 预约记录列表
     */
    @GetMapping("/private-station/reserve/list")
    public Result<?> queryPrivateStationReserveList(@RequestHeader("Authorization") String authorization,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        // 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 调用Service层方法查询私桩预约记录
        PageResult<PrivateStationReserveListVO> reserveList = privateStationUseService
                .queryPrivateStationReserveList(userId, status, pageNum, pageSize);

        // 返回查询结果
        return Result.success(reserveList, "查询成功");
    }
}