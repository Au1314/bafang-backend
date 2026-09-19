package com.baffang.admin.service;

import com.baffang.admin.dto.SystemLogQueryDTO;
import com.baffang.admin.vo.SystemLogDetailVO;
import com.baffang.admin.vo.SystemLogListVO;
import com.github.pagehelper.Page;

import java.util.List;

public interface SystemLogService {
    
    /**
     * 查询日志列表
     */
    Page<SystemLogListVO> getLogList(SystemLogQueryDTO query);
    
    /**
     * 查询日志详情
     */
    SystemLogDetailVO getLogDetail(Long id);
    
    /**
     * 获取所有模块名称
     */
    List<String> getModules();
}
