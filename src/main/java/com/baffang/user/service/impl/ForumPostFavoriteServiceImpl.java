package com.baffang.user.service.impl;

import com.baffang.common.entity.PostFavorite;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.user.mapper.UserForumPostFavoriteMapper;
import com.baffang.user.service.ForumPostFavoriteService;
import com.baffang.user.service.ForumPostService;
import com.baffang.user.vo.ForumPostListVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户端帖子收藏服务实现类
 */
@Service
public class ForumPostFavoriteServiceImpl implements ForumPostFavoriteService {

    @Autowired
    private UserForumPostFavoriteMapper userForumPostFavoriteMapper;

    @Autowired
    private ForumPostService forumPostService;

    /**
     * 收藏帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    @Override
    public Boolean favoritePost(Integer userId, Integer postId) {
        // 1. 检查帖子是否存在
        Boolean postExists = forumPostService.checkPostExists(postId);
        if (!postExists) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "帖子不存在");
        }

        // 2. 检查用户是否已收藏该帖子
        Integer count = userForumPostFavoriteMapper.checkUserFavoritePost(userId, postId);
        if (count != null && count > 0) {
            // 已收藏
            return true;
        }

        // 3. 插入收藏记录
        PostFavorite postFavorite = new PostFavorite();
        postFavorite.setUserId(userId);
        postFavorite.setPostId(postId);
        userForumPostFavoriteMapper.insertPostFavorite(postFavorite);

        // 4. 更新帖子收藏数
        userForumPostFavoriteMapper.increasePostCollectCount(postId);

        return true;
    }

    @Override
    public Boolean cancelFavoritePost(Integer userId, Integer postId) {
        // 1. 检查帖子是否存在
        Boolean postExists = forumPostService.checkPostExists(postId);
        if (!postExists) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "帖子不存在");
        }

        // 2. 检查用户是否已收藏该帖子
        Integer count = userForumPostFavoriteMapper.checkUserFavoritePost(userId, postId);
        if (count == null || count == 0) {
            // 未收藏，抛出异常
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "未收藏该帖子，无法取消收藏");
        }

        // 3. 取消收藏（将is_deleted设为1）
        userForumPostFavoriteMapper.cancelFavoritePost(userId, postId);

        // 4. 更新帖子收藏数（减一）
        userForumPostFavoriteMapper.decreasePostCollectCount(postId);

        return true;
    }

    @Override
    public PageResult<ForumPostListVO> getUserFavoritePosts(Integer userId, Integer pageNum, Integer pageSize) {
        // 设置默认值
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        // 开始分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询用户收藏的帖子列表
        List<ForumPostListVO> favoritePosts = userForumPostFavoriteMapper.getUserFavoritePosts(userId);

        // 转换为Page对象以获取分页信息
        Page<ForumPostListVO> page = (Page<ForumPostListVO>) favoritePosts;

        // 构造PageResult返回
        PageResult<ForumPostListVO> pageResult = new PageResult<>();
        pageResult.setTotal((int) page.getTotal());
        pageResult.setPages(page.getPages());
        pageResult.setPageNum(page.getPageNum());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setList(page.getResult());

        return pageResult;
    }
}