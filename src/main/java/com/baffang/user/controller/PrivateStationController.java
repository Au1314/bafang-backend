package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.HandlePrivateStationReserveDTO;
import com.baffang.user.dto.PrivateStationAuthDTO;
import com.baffang.user.dto.PrivateStationPriceDTO;
import com.baffang.user.dto.PrivateStationStatusDTO;
import com.baffang.user.dto.PrivateStationTimeConfigDTO;
import com.baffang.user.dto.PrivateStationDynamicPriceDTO;
import com.baffang.user.dto.PriceTypeDTO;
import com.baffang.user.service.PrivateStationService;
import com.baffang.user.vo.HandlePrivateStationReserveVO;
import com.baffang.user.vo.PriceConfigVO;
import com.baffang.user.vo.PrivateStationAuthProgressVO;
import com.baffang.user.vo.PrivateStationInfoVO;
import com.baffang.user.vo.ChargingPrivateStationInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 私桩控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class PrivateStationController {

    @Autowired
    private PrivateStationService privateStationService;

    /**
     * 私桩认证提交接口
     *
     * @param authorization         请求头中的Authorization令牌
     * @param privateStationAuthDTO 私桩认证请求参数
     * @return 提交结果
     */
    @PostMapping("/private-station/auth")
    public Result<?> submitPrivateStationAuth(@RequestHeader("Authorization") String authorization,
            @RequestBody PrivateStationAuthDTO privateStationAuthDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法提交私桩认证信息
        privateStationService.submitPrivateStationAuth(userId, privateStationAuthDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("私桩信息提交成功，等待审核");
        return result;
    }

    /**
     * 查询私桩列表接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param pageNum       页码（默认1）
     * @param pageSize      每页大小（默认10）
     * @return 私桩列表分页结果
     */
    @GetMapping("/private-station/list")
    public Result<?> getPrivateStationList(@RequestHeader("Authorization") String authorization,
            @RequestParam(value = "page_num", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法获取私桩列表
        com.baffang.common.result.PageResult<?> pageResult = privateStationService.getPrivateStationListByUserId(userId,
                pageNum, pageSize);

        // 4. 返回结果
        return Result.success(pageResult);
    }

    /**
     * 解绑私桩接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @return 解绑结果
     */
    @DeleteMapping("/private-station/{stationId}")
    public Result<?> unbindPrivateStation(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法解绑私桩
        privateStationService.unbindPrivateStation(userId, stationId);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("私桩解绑成功");
        return result;
    }

    /**
     * 设置私桩固定价格接口
     *
     * @param authorization          请求头中的Authorization令牌
     * @param stationId              私桩ID
     * @param privateStationPriceDTO 私桩价格设置请求参数
     * @return 设置结果
     */
    @PutMapping("/private-station/{stationId}/price/fixed")
    public Result<?> setPrivateStationFixedPrice(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId,
            @RequestBody PrivateStationPriceDTO privateStationPriceDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法设置私桩固定价格
        privateStationService.setPrivateStationFixedPrice(userId, stationId, privateStationPriceDTO.getPrice());

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("固定价格设置成功");
        return result;
    }

    /**
     * 查询私桩时间配置接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @return 私桩时间配置信息
     */
    @GetMapping("/private-station/{stationId}/time-config")
    public Result<?> getPrivateStationTimeConfig(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法查询私桩时间配置
        Object timeConfig = privateStationService.getPrivateStationTimeConfig(userId, stationId);

        // 4. 返回结果
        Result<?> result = Result.success(timeConfig);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 设置私桩启停时间接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @param timeConfigDTO 时间配置请求参数
     * @return 设置结果
     */
    @PutMapping("/private-station/{stationId}/time-config")
    public Result<?> setPrivateStationTimeConfig(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId,
            @RequestBody PrivateStationTimeConfigDTO timeConfigDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法设置私桩启停时间
        privateStationService.setPrivateStationTimeConfig(userId, stationId, timeConfigDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("启停时间设置成功");
        return result;
    }

    /**
     * 手动开关私桩接口
     *
     * @param authorization           请求头中的Authorization令牌
     * @param stationId               私桩ID
     * @param privateStationStatusDTO 私桩状态设置请求参数
     * @return 设置结果
     */
    @PutMapping("/private-station/{stationId}/status")
    public Result<?> setPrivateStationStatus(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId,
            @RequestBody PrivateStationStatusDTO privateStationStatusDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法设置私桩状态
        privateStationService.setPrivateStationStatus(userId, stationId, privateStationStatusDTO.getOpenStatus());

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg(privateStationStatusDTO.getOpenStatus() == 1 ? "私桩已开启" : "私桩已关闭");
        return result;
    }

    /**
     * 设置私桩动态价格
     *
     * @param authorization                 请求头中的Authorization令牌
     * @param stationId                     私桩ID
     * @param privateStationDynamicPriceDTO 私桩动态价格设置请求参数
     * @return 设置结果
     */
    @PostMapping("/private-station/{stationId}/price/dynamic")
    public Result<?> setPrivateStationDynamicPrice(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId,
            @RequestBody PrivateStationDynamicPriceDTO privateStationDynamicPriceDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法设置私桩动态价格
        privateStationService.setPrivateStationDynamicPrice(userId, stationId, privateStationDynamicPriceDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("动态价格规则设置成功");
        return result;
    }

    /**
     * 开启/关闭私桩动态价格
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @param priceTypeDTO  价格类型设置请求参数
     * @return 设置结果
     */
    @PutMapping("/private-station/{stationId}/price_type")
    public Result<?> setPrivateStationPriceType(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId,
            @RequestBody PriceTypeDTO priceTypeDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法开启/关闭私桩动态价格
        privateStationService.setPrivateStationPriceType(userId, stationId, priceTypeDTO.getPriceType());

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("成功开启/关闭动态价格");
        return result;
    }

    /**
     * 查询私桩价格配置
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @return 价格配置信息
     */
    @GetMapping("/private-station/{stationId}/price")
    public Result<?> getPrivateStationPriceConfig(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法查询私桩价格配置
        PriceConfigVO priceConfigVO = privateStationService.getPrivateStationPriceConfig(userId, stationId);

        // 4. 返回结果
        Result<?> result = Result.success(priceConfigVO);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 查询私桩详细信息接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @return 私桩详细信息
     */
    @GetMapping("/private-station/{stationId}/info")
    public Result<?> getPrivateStationInfo(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法查询私桩详细信息
        PrivateStationInfoVO privateStationInfoVO = privateStationService.getPrivateStationInfo(userId, stationId);

        // 4. 返回结果
        Result<?> result = Result.success(privateStationInfoVO);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 查询私桩详细信息接口（用户视角）
     *
     * @param stationId 私桩ID
     * @return 私桩详细信息（用户视角）
     */
    @GetMapping("/charging/private-station/{stationId}/info")
    public Result<?> getChargingPrivateStationInfo(@PathVariable("stationId") Integer stationId) {
        // 调用Service层方法查询私桩详细信息（用户视角）
        ChargingPrivateStationInfoVO chargingPrivateStationInfoVO = privateStationService
                .getChargingPrivateStationInfo(stationId);

        // 返回结果
        Result<?> result = Result.success(chargingPrivateStationInfoVO);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 删除私桩动态价格规则
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @param ruleId        规则ID
     * @return 删除结果
     */
    @DeleteMapping("/private-station/{stationId}/price/dynamic/{ruleId}")
    public Result<?> deletePrivateStationDynamicPriceRule(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId,
            @PathVariable("ruleId") Integer ruleId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法删除私桩动态价格规则
        privateStationService.deletePrivateStationDynamicPriceRule(userId, stationId, ruleId);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("动态价格规则已成功删除");
        return result;
    }

    /**
     * 重新提交私桩认证接口
     *
     * @param authorization         请求头中的Authorization令牌
     * @param stationId             私桩ID
     * @param privateStationAuthDTO 私桩认证请求参数
     * @return 提交结果
     */
    @PutMapping("/private-station/{stationId}/auth")
    public Result<?> resubmitPrivateStationAuth(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") Integer stationId,
            @RequestBody PrivateStationAuthDTO privateStationAuthDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法重新提交私桩认证信息
        privateStationService.resubmitPrivateStationAuth(userId, stationId, privateStationAuthDTO);

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("私桩认证重新提交成功，等待审核");
        return result;
    }

    /**
     * 查询私桩认证进度接口
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @return 私桩认证进度信息
     */
    @GetMapping("/private-station/{stationId}/auth-progress")
    public Result<?> getPrivateStationAuthProgress(@RequestHeader("Authorization") String authorization,
            @PathVariable("stationId") String stationId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法查询私桩认证进度
        PrivateStationAuthProgressVO authProgress = privateStationService.getPrivateStationAuthProgress(userId,
                Integer.valueOf(stationId));

        // 4. 返回结果
        Result<?> result = Result.success(authProgress);
        result.setMsg("私桩认证进度查询成功");
        return result;
    }

    /**
     * 查询私桩预约记录（桩主视角）
     *
     * @param authorization 请求头中的Authorization令牌
     * @param status        预约状态（可选）
     * @param pageNum       页码（默认1）
     * @param pageSize      每页条数（默认10）
     * @return 预约记录列表分页结果
     */
    @GetMapping("/private-station/reserve")
    public Result<?> queryPrivateStationReserve(@RequestHeader("Authorization") String authorization,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法查询私桩预约记录
        com.baffang.common.result.PageResult<?> pageResult = privateStationService.queryPrivateStationReserve(
                userId, status, pageNum, pageSize);

        // 4. 返回结果
        Result<?> result = Result.success(pageResult);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 处理私桩预约申请
     *
     * @param authorization 请求头中的Authorization令牌
     * @param reserveId     预约ID
     * @param requestBody   请求体，包含要更新的状态
     * @return 处理结果
     */
    @PutMapping("/private-station/reserve/{reserveId}/handle")
    public Result<?> handlePrivateStationReserve(@RequestHeader("Authorization") String authorization,
            @PathVariable("reserveId") Integer reserveId,
            @RequestBody HandlePrivateStationReserveDTO requestBody) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法处理预约申请
        HandlePrivateStationReserveVO result = privateStationService.handlePrivateStationReserve(userId, reserveId,
                requestBody.getStatus());

        // 4. 返回结果
        return Result.success(result);
    }
}
