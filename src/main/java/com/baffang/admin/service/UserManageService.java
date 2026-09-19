package com.baffang.admin.service;

import com.baffang.admin.dto.*;
import com.baffang.admin.vo.*;
import com.github.pagehelper.Page;

import java.util.List;

public interface UserManageService {
    
    Page<UserListVO> getUserList(UserQueryDTO queryDTO);
    
    UserDetailVO getUserDetail(Integer id);
    
    void updateUser(Integer id, UserUpdateDTO updateDTO);
    
    void updateUserStatus(Integer id, UserStatusDTO statusDTO);
    
    String resetPassword(Integer id);
    
    void deleteUser(Integer id);
    
    void batchDeleteUsers(List<Integer> ids);
    
    UserStatisticsVO getStatistics();
}
