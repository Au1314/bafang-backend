package com.baffang.admin.mapper;

import com.baffang.common.entity.AdminInviteCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminInviteCodeMapper {
    
    AdminInviteCode selectByCode(@Param("code") String code);
    
    int insert(AdminInviteCode inviteCode);
    
    int incrementUsedCount(@Param("id") Integer id);
    
    List<AdminInviteCode> selectByCreatedBy(@Param("createdBy") Integer createdBy);

    /**
 * 根据ID查询
 */
AdminInviteCode selectById(@Param("id") Integer id);

/**
 * 删除邀请码
 */
int deleteById(@Param("id") Integer id);

/**
 * 更新状态
 */
int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

}
