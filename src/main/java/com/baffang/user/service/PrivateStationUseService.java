package com.baffang.user.service;

import com.baffang.common.result.PageResult;
import com.baffang.user.dto.PrivateStationReserveDTO;
import com.baffang.user.vo.PrivateStationReserveListVO;
import com.baffang.user.vo.PrivateStationReserveVO;

/**
 * 私桩使用服务接口
 */
public interface PrivateStationUseService {
        /**
         * 提交私桩预约申请
         * 
         * @param userId     用户ID
         * @param stationId  私桩ID
         * @param reserveDTO 预约信息
         * @return 预约结果
         */
        PrivateStationReserveVO reservePrivateStation(Integer userId, Integer stationId,
                        PrivateStationReserveDTO reserveDTO);

        /**
         * 取消私桩预约
         * 
         * @param userId    用户ID
         * @param stationId 私桩ID
         */
        void cancelPrivateStationReserve(Integer userId, Integer stationId);

        /**
         * 查询个人私桩预约记录
         * 
         * @param userId   用户ID
         * @param status   预约状态（可选）
         * @param pageNum  页码
         * @param pageSize 每页条数
         * @return 预约记录列表
         */
        PageResult<PrivateStationReserveListVO> queryPrivateStationReserveList(Integer userId, Integer status,
                        Integer pageNum, Integer pageSize);
}