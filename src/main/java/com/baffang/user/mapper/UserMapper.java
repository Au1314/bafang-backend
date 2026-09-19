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

}
