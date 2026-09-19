package com.baffang.user.service;

import java.util.Map;
import com.baffang.user.dto.LongRoutePlanDTO;
import com.baffang.user.dto.LongRoutePlanDTO20;
import com.baffang.user.dto.ShortRoutePlanDTO;
import com.baffang.user.vo.LongRoutePlanVO;
import com.baffang.user.vo.LongRoutePlanVO20;
import com.baffang.user.vo.ShortRoutePlanVO;

/**
 * 导航服务接口
 */
public interface NaviService {

    /**
     * 获取用户半径范围内的所有私桩
     *
     * @param keyword    搜索关键词（可选）
     * @param currentLng 当前经度（可选）
     * @param currentLat 当前纬度（可选）
     * @param radius     搜索半径（可选，默认3公里，最大10公里）
     * @return 包含私桩列表和总数的结果
     */
    Map<String, Object> searchPrivateStations(String keyword, Double currentLng, Double currentLat, Integer radius);

    /**
     * 智能短途路线规划
     * 
     * @param dto 路线规划请求参数
     * @return 包含推荐充电桩和备选充电桩的路线规划结果
     */
    ShortRoutePlanVO planShortRoute(ShortRoutePlanDTO dto);

    /**
     * 智能长途路径规划
     * 
     * @param dto 长途路径规划请求参数
     * @return 包含多个备选路线方案的规划结果
     */
    LongRoutePlanVO planLongRoute(LongRoutePlanDTO dto);

    /**
     * 智能长途路径规划 2.0
     * 
     * @param dto 长途路径规划请求参数
     * @return 包含多个备选路线方案的规划结果
     */
    LongRoutePlanVO20 planLongRoute20(LongRoutePlanDTO20 dto);
}