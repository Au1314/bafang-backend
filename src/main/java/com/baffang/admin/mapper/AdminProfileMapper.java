package com.baffang.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AdminProfileMapper {
    
    /**
     * 根据ID查询管理员信息
     */
    Map<String, Object> selectById(@Param("id") Integer id);
    
    /**
     * 查询密码
     */
    String selectPasswordById(@Param("id") Integer id);
    
    /**
     * 更新个人信息
     */
    int updateProfile(@Param("id") Integer id, 
                      @Param("name") String name, 
                      @Param("avatar") String avatar);
    
    /**
     * 更新密码
     */
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
    
    /**
     * 更新头像
     */
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);
}
