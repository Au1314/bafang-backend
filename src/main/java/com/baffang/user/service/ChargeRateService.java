package com.baffang.user.service;

import com.baffang.common.result.PageResult;
import com.baffang.user.dto.ChargeRateDTO;
import com.baffang.user.vo.ChargeRateVO;

/**
 * 充电桩评论服务接口
 */
public interface ChargeRateService {
    /**
     * 发布充电桩评论
     * 
     * @param userId        用户ID
     * @param chargeRateDTO 评论发布请求参数
     */
    void publishChargeRate(Integer userId, ChargeRateDTO chargeRateDTO);

    /**
     * 查询充电桩评论列表
     * 
     * @param stationId   充电桩ID
     * @param stationType 桩类型：0=公桩，1=私桩
     * @param pageNum     页码
     * @param pageSize    每页条数
     * @return 评论列表分页结果
     */
    PageResult<ChargeRateVO> queryChargeRateList(Integer stationId, Integer stationType, Integer pageNum,
            Integer pageSize);

    /**
     * 删除充电桩评论
     * 
     * @param userId 用户ID
     * @param rateId 评论ID
     */
    void deleteChargeRate(Integer userId, Integer rateId);
}