package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.ChargeRateDTO;
import com.baffang.user.service.ChargeRateService;
import com.baffang.user.vo.ChargeRateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 充电桩评论控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class ChargeRateController {

    @Autowired
    private ChargeRateService chargeRateService;

    /**
     * 发布充电桩评论
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param chargeRateDTO 评论发布请求参数
     * @return 发布结果
     */
    @PostMapping("/charging/rate")
    public Result<?> publishChargeRate(@RequestHeader("Authorization") String authorization,
            @RequestBody ChargeRateDTO chargeRateDTO) {
        // 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 调用Service层方法发布评论
        chargeRateService.publishChargeRate(userId, chargeRateDTO);

        // 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("评论发布成功");
        return result;
    }

    /**
     * 查询充电桩评论列表
     * 
     * @param stationId 充电桩ID
     * @param stationType 桩类型：0=公桩，1=私桩
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 评论列表分页结果
     */
    @GetMapping("/charging/rate/{stationId}/{stationType}")
    public Result<?> queryChargeRateList(@PathVariable("stationId") Integer stationId,
            @PathVariable("stationType") Integer stationType,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 调用Service层方法查询评论列表
        return Result.success(chargeRateService.queryChargeRateList(stationId, stationType, pageNum, pageSize));
    }
    
    /**
     * 删除充电桩评论
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param rateId 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/charging/rate/{rateId}")
    public Result<?> deleteChargeRate(@RequestHeader("Authorization") String authorization,
            @PathVariable("rateId") String rateId) {
        // 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        
        // 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        
        // 将rateId转换为Integer类型
        Integer rateIdInt = Integer.valueOf(rateId);
        
        // 调用Service层方法删除评论
        chargeRateService.deleteChargeRate(userId, rateIdInt);
        
        // 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("评论已删除");
        return result;
    }
}