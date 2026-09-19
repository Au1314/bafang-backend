package com.baffang.user.service;

import com.baffang.common.result.PageResult;
import com.baffang.user.dto.PrivateStationAuthDTO;
import com.baffang.user.dto.PrivateStationDynamicPriceDTO;
import com.baffang.user.dto.PrivateStationTimeConfigDTO;
import com.baffang.user.vo.PrivateStationListVO;
import com.baffang.user.vo.PrivateStationOwnerReserveListVO;
import com.baffang.user.vo.PrivateStationTimeConfigVO;
import com.baffang.user.vo.PrivateStationAuthProgressVO;
import com.baffang.user.vo.PrivateStationInfoVO;
import com.baffang.user.vo.PriceConfigVO;

/**
 * 私桩服务接口
 */
public interface PrivateStationService {

        /**
         * 提交私桩认证信息
         *
         * @param userId                用户ID
         * @param privateStationAuthDTO 私桩认证请求参数
         */
        void submitPrivateStationAuth(Integer userId, PrivateStationAuthDTO privateStationAuthDTO);

        /**
         * 根据用户ID查询私桩列表（分页）
         *
         * @param userId   用户ID
         * @param pageNum  页码
         * @param pageSize 每页大小
         * @return 私桩列表分页结果
         */
        PageResult<PrivateStationListVO> getPrivateStationListByUserId(Integer userId, Integer pageNum,
                        Integer pageSize);

        /**
         * 解绑私桩
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         */
        void unbindPrivateStation(Integer userId, Integer stationId);

        /**
         * 设置私桩固定价格
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         * @param price     固定价格（元/度）
         */
        void setPrivateStationFixedPrice(Integer userId, Integer stationId, Double price);

        /**
         * 查询私桩时间配置
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         * @return 私桩时间配置信息
         */
        PrivateStationTimeConfigVO getPrivateStationTimeConfig(Integer userId, Integer stationId);

        /**
         * 设置私桩启停时间
         *
         * @param userId        用户ID
         * @param stationId     私桩ID
         * @param timeConfigDTO 时间配置参数
         */
        void setPrivateStationTimeConfig(Integer userId, Integer stationId, PrivateStationTimeConfigDTO timeConfigDTO);

        /**
         * 设置私桩状态（手动开关）
         *
         * @param userId     用户ID
         * @param stationId  私桩ID
         * @param openStatus 开关状态，0=关闭，1=开启
         */
        void setPrivateStationStatus(Integer userId, Integer stationId, Integer openStatus);

        /**
         * 设置私桩动态价格
         *
         * @param userId                        用户ID
         * @param stationId                     私桩ID
         * @param privateStationDynamicPriceDTO 私桩动态价格设置请求参数
         */
        void setPrivateStationDynamicPrice(Integer userId, Integer stationId,
                        PrivateStationDynamicPriceDTO privateStationDynamicPriceDTO);

        /**
         * 开启/关闭私桩动态价格
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         * @param priceType 价格类型：0-静态价格，1-动态价格
         */
        void setPrivateStationPriceType(Integer userId, Integer stationId, Integer priceType);

        /**
         * 查询私桩价格配置
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         * @return 价格配置信息
         */
        com.baffang.user.vo.PriceConfigVO getPrivateStationPriceConfig(Integer userId, Integer stationId);

        /**
         * 删除私桩动态价格规则
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         * @param ruleId    规则ID
         */
        void deletePrivateStationDynamicPriceRule(Integer userId, Integer stationId, Integer ruleId);

        /**
         * 查询私桩预约记录（桩主视角）
         *
         * @param userId   用户ID（桩主ID）
         * @param status   预约状态（可选）
         * @param pageNum  页码
         * @param pageSize 每页条数
         * @return 预约记录列表
         */
        PageResult<PrivateStationOwnerReserveListVO> queryPrivateStationReserve(Integer userId, Integer status,
                        Integer pageNum, Integer pageSize);

        /**
         * 处理私桩预约申请
         *
         * @param userId    用户ID（桩主ID）
         * @param reserveId 预约ID
         * @param status    操作类型：1=确认，2=拒绝
         * @return 处理结果
         */
        com.baffang.user.vo.HandlePrivateStationReserveVO handlePrivateStationReserve(Integer userId, Integer reserveId,
                        Integer status);

        /**
         * 重新提交私桩认证
         *
         * @param userId                用户ID
         * @param stationId             私桩ID
         * @param privateStationAuthDTO 私桩认证请求参数
         */
        void resubmitPrivateStationAuth(Integer userId, Integer stationId, PrivateStationAuthDTO privateStationAuthDTO);

        /**
         * 查询私桩认证进度
         *
         * @param userId    用户ID
         * @param stationId 私桩ID
         * @return 私桩认证进度信息
         */
        PrivateStationAuthProgressVO getPrivateStationAuthProgress(Integer userId, Integer stationId);

            /**
     * 查询私桩详细信息
     *
     * @param userId    用户ID
     * @param stationId 私桩ID
     * @return 私桩详细信息
     */
    PrivateStationInfoVO getPrivateStationInfo(Integer userId, Integer stationId);

    /**
     * 查询私桩详细信息（用户视角）
     *
     * @param stationId 私桩ID
     * @return 私桩详细信息（用户视角）
     */
    com.baffang.user.vo.ChargingPrivateStationInfoVO getChargingPrivateStationInfo(Integer stationId);
}
