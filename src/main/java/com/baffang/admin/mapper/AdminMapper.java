package com.baffang.admin.mapper;

import com.baffang.common.entity.AdminInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdminMapper {

    AdminInfo selectByUsername(@Param("username") String username);

    AdminInfo selectById(@Param("id") Long id);

    int insert(AdminInfo adminInfo);

    int updateLastLoginTime(@Param("id") Integer id);

    // ========== 新增方法 ==========

    /**
     * 更新管理员信息
     */
    int updateById(AdminInfo adminInfo);

    /**
     * 分页查询管理员列表
     */
    List<AdminInfo> selectList(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计管理员总数
     */
    int countAll();

    List<AdminInfo> selectCustomerServiceAdmins();
}
