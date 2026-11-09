package com.baffang.user.mapper;

import com.baffang.common.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserInfo selectByUsername(@Param("username") String username);

    UserInfo selectById(@Param("id") Integer id);

    UserInfo selectByPhone(@Param("phone") String phone);

    int insert(UserInfo userInfo);

    /**
     * 统计用户总数
     */
    int countTotalUsers();

    /**
     * 统计今日活跃用户数（今日登录过的用户）
     */
    int countDailyActiveUsers(@Param("date") String date);

    /**
     * 更新用户充电桩认证状态
     */
    int updateAuthChargingStation(@Param("id") Integer id, @Param("authStatus") Integer authStatus);

    /**
     * 更新用户个人信息
     * 
     * @param userInfo 用户信息对象
     * @return 更新影响的行数
     */
    int updateProfile(UserInfo userInfo);

    /**
     * 更新用户密码
     * 
     * @param id       用户ID
     * @param password 加密后的密码
     * @return 更新影响的行数
     */
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    /**
     * 更新用户隐私设置
     *
     * @param userInfo 用户信息对象
     * @return 更新影响的行数
     */
    int updatePrivacySettings(UserInfo userInfo);

    /**
     * 扣减余额（订单支付，契约补齐）
     * 乐观条件：余额充足才扣减，影响行数 0 表示余额不足
     */
    @org.apache.ibatis.annotations.Update(
            "UPDATE user_info SET balance = balance - #{amount}, update_time = NOW() " +
            "WHERE id = #{userId} AND is_deleted = 0 AND balance >= #{amount}")
    int deductBalance(@Param("userId") Integer userId, @Param("amount") java.math.BigDecimal amount);

    /**
     * 扣减积分（订单积分抵扣，契约补齐；1 积分抵 1 元）
     */
    @org.apache.ibatis.annotations.Update(
            "UPDATE user_info SET points = points - #{points}, update_time = NOW() " +
            "WHERE id = #{userId} AND is_deleted = 0 AND points >= #{points}")
    int deductPoints(@Param("userId") Integer userId, @Param("points") Integer points);

    /**
     * 增加积分（充电完成按电量奖励碳积分，契约补齐）
     */
    @org.apache.ibatis.annotations.Update(
            "UPDATE user_info SET points = points + #{points}, update_time = NOW() " +
            "WHERE id = #{userId} AND is_deleted = 0")
    int addPoints(@Param("userId") Integer userId, @Param("points") Integer points);

}
