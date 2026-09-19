package com.baffang.user.controller;

import com.baffang.common.result.Result;
import com.baffang.user.dto.LongRoutePlanDTO;
import com.baffang.user.dto.LongRoutePlanDTO20;
import com.baffang.user.dto.ShortRoutePlanDTO;
import com.baffang.user.service.NaviService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 导航控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class NaviController {

    @Autowired
    @Qualifier("naviServiceImpl")
    private NaviService naviService;

    @Autowired
    @Qualifier("naviService20")
    private NaviService naviService20;

    /**
     * 获取用户半径范围内的所有私桩
     *
     * @param authorization 请求头中的Authorization令牌
     * @param keyword       搜索关键词（可选）
     * @param currentLng    当前经度（可选）
     * @param currentLat    当前纬度（可选）
     * @param radius        搜索半径（可选，默认3公里，最大10公里）
     * @return 私桩列表结果
     */
    @GetMapping("/navi/search/private")
    public Result<?> searchPrivateStations(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "currentLng", required = false) Double currentLng,
            @RequestParam(value = "currentLat", required = false) Double currentLat,
            @RequestParam(value = "radius", required = false) Integer radius) {
        // 调用Service层方法获取私桩列表
        Object result = naviService.searchPrivateStations(keyword, currentLng, currentLat, radius);

        // 返回结果
        return Result.success(result);
    }

    /**
     * 智能短途路线规划
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param dto           路线规划请求参数
     * @return 包含推荐充电桩和备选充电桩的路线规划结果
     */
    @PostMapping("/navi/short-route")
    public Result<?> planShortRoute(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody ShortRoutePlanDTO dto) {
        // 调用Service层方法进行路线规划
        Object result = naviService.planShortRoute(dto);

        // 返回结果
        return Result.success(result);
    }

    /**
     * 智能长途路径规划
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param dto           长途路径规划请求参数
     * @return 包含多个备选路线方案的规划结果
     */
    @PostMapping("/navi/long-route")
    public Result<?> planLongRoute(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody LongRoutePlanDTO dto) {
        // 调用Service层方法进行长途路径规划
        Object result = naviService.planLongRoute(dto);

        // 返回结果
        return Result.success(result);
    }

    /**
     * 智能长途路径规划 2.0
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param dto           长途路径规划请求参数
     * @return 包含多个备选路线方案的规划结果
     */
    @PostMapping("/navi/long-route20")
    public Result<?> planLongRoute20(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody LongRoutePlanDTO20 dto) {
        // 调用Service层方法进行长途路径规划
        Object result = naviService20.planLongRoute20(dto);

        // 返回结果
        return Result.success(result);
    }
}