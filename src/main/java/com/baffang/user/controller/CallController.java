package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.EmergencyCallDTO;
import com.baffang.user.dto.V2VCallDTO;
import com.baffang.user.dto.V2VResponseDTO;
import com.baffang.user.service.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 呼叫模块控制器
 * 处理V2V充电呼叫相关接口
 */
@RestController
@RequestMapping("/api/v1/user")
public class CallController {

    @Autowired
    private CallService callService;

    /**
     * 发起V2V充电呼叫
     * 
     * @param authorization 认证令牌
     * @param v2vCallDTO    呼叫请求参数
     * @return 响应结果
     */
    @PostMapping("/call/v2v")
    public Result<?> initiateV2VCall(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody V2VCallDTO v2vCallDTO) {
        // 从token中获取用户ID
        Integer userId = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
            userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        }

        callService.initiateV2VCall(userId, v2vCallDTO);
        return Result.success(null);
    }

    /**
     * 取消V2V充电呼叫
     * 将用户所有待响应（0）和已响应（1）的呼叫设为已取消（3）
     * 
     * @param authorization 认证令牌
     * @return 响应结果
     */
    @DeleteMapping("/call/v2v/")
    public Result<?> cancelV2VCall(@RequestHeader("Authorization") String authorization) {
        // 从token中获取用户ID
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return Result.error("无效的认证令牌");
        }

        String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        callService.cancelV2VCall(userId);
        return Result.success(null, "V2V呼叫已取消");
    }

    /**
     * 取消紧急救援呼叫
     * 将用户所有待响应（0）和已响应（1）的紧急呼叫设为已取消（3）
     * 
     * @param authorization 认证令牌
     * @return 响应结果
     */
    @DeleteMapping("/call/emergency/")
    public Result<?> cancelEmergencyCall(@RequestHeader("Authorization") String authorization) {
        // 从token中获取用户ID
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return Result.error("无效的认证令牌");
        }

        String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        callService.cancelEmergencyCall(userId);
        return Result.success(null, "紧急救援呼叫已取消");
    }

    /**
     * 查询半径内所有待响应呼叫
     * 
     * @param currentLng 当前经度（可选）
     * @param currentLat 当前纬度（可选）
     * @param radius     搜索半径（可选，默认3公里，最大10公里）
     * @return 待响应呼叫列表结果
     */
    @GetMapping("/call/search")
    public Result<?> searchPendingCalls(@RequestParam(value = "currentLng", required = false) Double currentLng,
            @RequestParam(value = "currentLat", required = false) Double currentLat,
            @RequestParam(value = "radius", required = false) Integer radius) {
        // 调用Service层方法获取待响应呼叫列表
        Object result = callService.searchPendingCallsByRadius(currentLng, currentLat, radius);

        // 返回结果
        return Result.success(result);
    }

    /**
     * 响应V2V充电呼叫
     * 
     * @param authorization  认证令牌
     * @param callId         呼叫ID
     * @param v2vResponseDTO 响应请求参数
     * @return 响应结果
     */
    @PutMapping("/call/v2v/{callId}/response")
    public Result<?> responseV2VCall(@RequestHeader("Authorization") String authorization,
            @PathVariable("callId") Integer callId,
            @RequestBody V2VResponseDTO v2vResponseDTO) {
        // 从token中获取用户ID
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return Result.error("无效的认证令牌");
        }

        String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        callService.responseV2VCall(userId, callId, v2vResponseDTO);
        return Result.success(null, "已成功响应V2V呼叫，可与对方沟通详情");
    }

    /**
     * 创建与呼叫者的对话
     * 
     * @param authorization 认证令牌
     * @param callId        呼叫ID
     * @return 响应结果
     */
    @PostMapping("/call/v2v/{callId}/conversation")
    public Result<?> createConversation(@RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("callId") Integer callId) {
        // 从token中获取用户ID
        Integer userId = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
            userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        }

        callService.createConversationForV2VCall(userId, callId);
        return Result.success(null, "对话创建成功");
    }

    /**
     * 查询V2V呼叫记录
     * 
     * @param authorization 认证令牌
     * @param role          角色：0 = 发起者，1 = 响应者（可选）
     * @param status        状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消（可选）
     * @param pageNum       页码（可选，默认1）
     * @param pageSize      每页条数（可选，默认10）
     * @return 呼叫记录列表
     */
    @GetMapping("/call/v2v/list")
    public Result<?> queryCallRecords(@RequestHeader("Authorization") String authorization,
            @RequestParam(value = "role", required = false) Integer role,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 从token中获取用户ID
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return Result.error("无效的认证令牌");
        }

        String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 查询呼叫记录
        Object result = callService.queryV2VCallRecords(userId, role, status, pageNum, pageSize);

        // 返回结果
        return Result.success(result, "查询成功");
    }

    /**
     * 查询紧急呼叫记录
     * 
     * @param authorization 认证令牌
     * @param callType      呼叫类型：0 = 拖车，1 = 充电桩，2 = 其他（可选）
     * @param status        状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消（可选）
     * @param pageNum       页码（可选，默认1）
     * @param pageSize      每页条数（可选，默认10）
     * @return 分页的紧急呼叫记录列表
     */
    @GetMapping("/call/emergency/list")
    public Result<?> queryEmergencyCallRecords(@RequestHeader("Authorization") String authorization,
            @RequestParam(value = "callType", required = false) Integer callType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        // 从token中获取用户ID
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return Result.error("无效的认证令牌");
        }

        String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 查询紧急呼叫记录
        Object result = callService.queryEmergencyCallRecords(userId, callType, status, pageNum, pageSize);

        // 返回结果
        return Result.success(result, "查询成功");
    }

    /**
     * 发起紧急救援呼叫
     * 
     * @param authorization    认证令牌
     * @param emergencyCallDTO 紧急呼叫请求参数
     * @return 响应结果
     */
    @PostMapping("/call/emergency")
    public Result<?> initiateEmergencyCall(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody EmergencyCallDTO emergencyCallDTO) {
        // 从token中获取用户ID
        Integer userId = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
            userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        }

        callService.initiateEmergencyCall(userId, emergencyCallDTO);
        return Result.success(null, "紧急救援呼叫发起成功，工作人员将尽快联系你");
    }

    /**
     * 查询紧急呼叫进度
     * 
     * @param authorization 认证令牌
     * @return 紧急呼叫进度信息
     */
    @GetMapping("/call/emergency/progress")
    public Result<?> queryEmergencyCallProgress(@RequestHeader("Authorization") String authorization) {
        // 从token中获取用户ID
        if (authorization == null || !authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return Result.error("无效的认证令牌");
        }

        String token = authorization.substring(JwtConstants.TOKEN_PREFIX.length());
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 查询紧急呼叫进度
        Object result = callService.queryEmergencyCallProgress(userId);

        // 返回结果
        return Result.success(result, "查询成功");
    }
}
