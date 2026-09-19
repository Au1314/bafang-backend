package com.baffang.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baffang.admin.dto.AdminNoticeDTO;
import com.baffang.common.entity.SystemNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统通知 Mapper
 */
@Mapper
public interface SystemNoticeMapper extends BaseMapper<SystemNotice> {

    /**
     * 管理端：查询所有通知（分页，关联用户信息）
     */
    List<AdminNoticeDTO> selectAllNotices(@Param("type") Integer type,
                                          @Param("userId") Integer userId,
                                          @Param("isRead") Integer isRead,
                                          @Param("keyword") String keyword,
                                          @Param("offset") Integer offset,
                                          @Param("pageSize") Integer pageSize);

    /**
     * 管理端：统计所有通知数量
     */
    int countAllNotices(@Param("type") Integer type,
                        @Param("userId") Integer userId,
                        @Param("isRead") Integer isRead,
                        @Param("keyword") String keyword);

    /**
     * 根据ID查询通知详情（关联用户信息）
     */
    AdminNoticeDTO selectDetailById(@Param("id") Integer id);

    /**
     * 批量插入通知
     */
    int batchInsert(@Param("list") List<SystemNotice> list);

    /**
     * 管理端：删除通知（软删除）
     */
    int deleteByAdmin(@Param("id") Integer id);

    /**
     * 管理端：批量删除通知（软删除）
     */
    int batchDeleteByAdmin(@Param("ids") List<Integer> ids);
}
