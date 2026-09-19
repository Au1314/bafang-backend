package com.baffang.admin.service.impl;

import com.baffang.admin.dto.SystemLogQueryDTO;
import com.baffang.admin.mapper.SystemLogMapper;
import com.baffang.admin.service.SystemLogService;
import com.baffang.admin.vo.SystemLogDetailVO;
import com.baffang.admin.vo.SystemLogListVO;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemLogServiceImpl implements SystemLogService {

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public Page<SystemLogListVO> getLogList(SystemLogQueryDTO query) {
        PageHelper.startPage(query.getPage(), query.getSize());
        return (Page<SystemLogListVO>) systemLogMapper.selectList(query);
    }

    @Override
    public SystemLogDetailVO getLogDetail(Long id) {
        SystemLogDetailVO detail = systemLogMapper.selectDetailById(id);
        if (detail == null) {
            throw new BusinessException(ErrorMsg.DATA_NOT_FOUND);
        }
        return detail;
    }

    @Override
    public List<String> getModules() {
        return systemLogMapper.selectDistinctModules();
    }
}
