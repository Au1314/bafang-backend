package com.baffang.user.mapper;

import java.util.List;

import com.baffang.common.entity.Conversation;
import com.baffang.user.vo.ConversationListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户端对话Mapper接口
 */
@Mapper
public interface UserConversationMapper {

    /**
     * 插入对话记录
     *
     * @param conversation 对话实体
     * @return 插入成功的记录数
     */
    int insertConversation(Conversation conversation);

    /**
     * 查询用户的对话列表
     *
     * @param userId 用户ID
     * @return 对话列表VO
     */
    List<ConversationListVO> selectConversationListByUserId(Integer userId);

    /**
     * 更新对话状态
     *
     * @param id     对话ID
     * @param status 新状态
     * @return 更新成功的记录数
     */
    int updateConversationStatus(@Param("id") Integer id, @Param("status") Integer status);
}