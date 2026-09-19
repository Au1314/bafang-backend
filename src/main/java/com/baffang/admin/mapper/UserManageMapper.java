package com.baffang.admin.mapper;

import com.baffang.admin.dto.UserQueryDTO;
import com.baffang.admin.dto.UserUpdateDTO;
import com.baffang.admin.vo.UserListVO;
import com.baffang.admin.vo.UserStatisticsVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserManageMapper {

    Page<UserListVO> selectUserList(UserQueryDTO queryDTO);

    Map<String, Object> selectUserDetail(@Param("id") Integer id);

    boolean existsById(@Param("id") Integer id);

    Integer selectIdByPhone(@Param("phone") String phone);

    void updateUser(@Param("id") Integer id, @Param("dto") UserUpdateDTO dto);

    void updateUserDeleted(@Param("id") Integer id, @Param("isDeleted") Integer isDeleted);

    void updatePassword(@Param("id") Integer id, @Param("password") String password);

    void batchUpdateDeleted(@Param("ids") List<Integer> ids, @Param("isDeleted") Integer isDeleted);

    UserStatisticsVO selectStatistics();

    Integer countUserStations(@Param("userId") Integer userId);

    Integer countUserVehicles(@Param("userId") Integer userId);

    Integer countUserOrders(@Param("userId") Integer userId);

    // 新增真删除方法
int deleteById(@Param("id") Integer id);
int batchDelete(@Param("ids") List<Integer> ids);
}
