package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.CarAuthDTO;
import com.baffang.user.service.CarAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 车辆认证控制器
 */
@RestController
public class CarAuthController {

    @Autowired
    private CarAuthService carAuthService;

    /**
     * 新增车辆认证
     *
     * @param authorization 请求头中的Authorization令牌
     * @param carAuthDTO    车辆认证请求参数
     * @return 新增结果
     */
    @PostMapping("/api/v1/user/car/auth-car")
    public Result<?> addCarAuth(@RequestHeader("Authorization") String authorization,
            @RequestBody CarAuthDTO carAuthDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法新增车辆认证
        carAuthService.addCarAuth(userId, carAuthDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("新增车辆认证成功，请等待管理员审核");
        return result;
    }

    /**
     * 查询车辆认证信息
     *
     * @param authorization 请求头中的Authorization令牌
     * @return 车辆认证信息
     */
    @GetMapping("/api/v1/user/car/info")
    public Result<?> getCarAuthInfo(@RequestHeader("Authorization") String authorization) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法获取车辆认证信息
        Object carAuth = carAuthService.getCarAuthByUserId(userId);

        // 4. 返回结果
        Result<?> result = Result.success(carAuth);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 删除车辆认证信息
     *
     * @param authorization 请求头中的Authorization令牌
     * @return 删除结果
     */
    @DeleteMapping("/api/v1/user/car/info")
    public Result<?> deleteCarAuth(@RequestHeader("Authorization") String authorization) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法删除车辆认证信息
        carAuthService.deleteCarAuth(userId);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("删除成功");
        return result;
    }

    /**
     * 更新车辆认证信息
     *
     * @param authorization 请求头中的Authorization令牌
     * @param carAuthDTO    车辆认证更新参数
     * @return 更新结果
     */
    @PutMapping("/api/v1/user/car/info")
    public Result<?> updateCarAuth(@RequestHeader("Authorization") String authorization, 
                                 @RequestBody CarAuthDTO carAuthDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法更新车辆认证信息
        carAuthService.updateCarAuth(userId, carAuthDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("更新成功");
        return result;
    }
}