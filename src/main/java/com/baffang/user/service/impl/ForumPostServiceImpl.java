package com.baffang.user.service.impl;

import com.baffang.common.entity.ForumPost;
import com.baffang.common.entity.PostLike;
import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.result.PageResult;
import com.baffang.user.mapper.UserForumPostFavoriteMapper;
import com.baffang.user.mapper.UserForumPostLikeMapper;
import com.baffang.user.mapper.UserForumPostMapper;
import com.baffang.user.mapper.UserForumPostReplyMapper;
import com.baffang.user.service.ForumPostService;
import com.baffang.user.vo.ForumPostListVO;
import com.baffang.user.vo.PostDetailResponseVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 用户端帖子服务实现类
 */
@Service
public class ForumPostServiceImpl implements ForumPostService {

    @Autowired
    private UserForumPostMapper userForumPostMapper;

    @Autowired
    private UserForumPostReplyMapper userForumPostReplyMapper;

    @Autowired
    private UserForumPostLikeMapper userForumPostLikeMapper;

    @Autowired
    private UserForumPostFavoriteMapper userForumPostFavoriteMapper;

    /**
     * 发布帖子
     * 
     * @param forumPost 帖子实体
     * @return 帖子ID
     */
    @Override
    public Integer publishPost(ForumPost forumPost) {
        userForumPostMapper.insertPost(forumPost);
        return forumPost.getId();
    }

    /**
     * 获取帖子列表
     * 
     * @param pageNum  页码，默认1
     * @param pageSize 每页条数，默认10
     * @param sort     排序方式：new=最新发布 /hot=热门（即点赞数）
     * @param keyword  关键字搜索
     * @return 帖子列表
     */
    @Override
    public PageResult<ForumPostListVO> getPostList(Integer pageNum, Integer pageSize, String sort, String keyword) {
        // 设置默认值
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (sort == null || sort.isEmpty()) {
            sort = "new";
        }

        // 开始分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询帖子列表（返回Map对象）
        List<Map<String, Object>> postMaps = userForumPostMapper.getPostList(sort, keyword);
        List<ForumPostListVO> postList = new ArrayList<>();

        // 处理图片列表并转换为ForumPostListVO对象
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        for (Map<String, Object> postMap : postMaps) {
            ForumPostListVO postVO = new ForumPostListVO();

            // 设置基本字段
            postVO.setPostId((Integer) postMap.get("postId"));
            postVO.setUserId((Integer) postMap.get("userId"));
            postVO.setTitle((String) postMap.get("title"));
            postVO.setContent((String) postMap.get("content"));
            postVO.setCreateTime((String) postMap.get("createTime"));
            postVO.setLikeCount((Integer) postMap.get("likeCount"));
            postVO.setCollectCount((Integer) postMap.get("collectCount"));
            postVO.setPublisherName((String) postMap.get("publisherName"));
            postVO.setPublisherAvatar((String) postMap.get("publisherAvatar"));

            // 处理图片列表
            String imagesJson = (String) postMap.get("images");
            if (imagesJson != null && !imagesJson.isEmpty()) {
                try {
                    List<String> imagesList = objectMapper.readValue(imagesJson,
                            new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                            });
                    postVO.setImages(imagesList);
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    e.printStackTrace();
                    postVO.setImages(new ArrayList<String>());
                }
            } else {
                postVO.setImages(new ArrayList<String>());
            }

            postList.add(postVO);
        }

        // 转换为Page对象以获取分页信息
        Page<Map<String, Object>> page = (Page<Map<String, Object>>) postMaps;

        // 构造PageResult返回
        PageResult<ForumPostListVO> pageResult = new PageResult<>();
        pageResult.setTotal((int) page.getTotal());
        pageResult.setPages(page.getPages());
        pageResult.setPageNum(page.getPageNum());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setList(postList);

        return pageResult;
    }

    /**
     * 点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 更新后的点赞数
     */
    @Override
    public Integer likePost(Integer userId, Integer postId) {
        // 1. 检查帖子是否存在
        Boolean postExists = checkPostExists(postId);
        if (!postExists) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "帖子不存在");
        }

        // 2. 检查用户是否已点赞该帖子
        Integer count = userForumPostLikeMapper.checkUserLikePost(userId, postId);
        if (count != null && count > 0) {
            // 已点赞
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "您已点赞该帖子");
        }

        // 3. 插入点赞记录
        PostLike postLike = new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);
        userForumPostLikeMapper.insertPostLike(postLike);

        // 4. 更新帖子点赞数
        userForumPostLikeMapper.increasePostLikeCount(postId);

        // 5. 获取更新后的点赞数
        return userForumPostLikeMapper.getPostLikeCount(postId);
    }

    /**
     * 取消点赞帖子
     * 
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 取消点赞后的点赞数
     */
    @Override
    public Integer cancelLikePost(Integer userId, Integer postId) {
        // 1. 检查帖子是否存在
        Boolean postExists = checkPostExists(postId);
        if (!postExists) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "帖子不存在");
        }

        // 2. 检查用户是否已点赞该帖子
        Integer count = userForumPostLikeMapper.checkUserLikePost(userId, postId);
        if (count == null || count == 0) {
            // 未点赞
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "您未点赞该帖子");
        }

        // 3. 取消点赞
        userForumPostLikeMapper.cancelPostLike(userId, postId);

        // 4. 减少帖子点赞数
        userForumPostLikeMapper.decreasePostLikeCount(postId);

        // 5. 获取更新后的点赞数
        return userForumPostLikeMapper.getPostLikeCount(postId);
    }

    /**
     * 检查帖子是否存在
     * 
     * @param postId 帖子ID
     * @return 是否存在
     */
    @Override
    public Boolean checkPostExists(Integer postId) {
        Integer count = userForumPostMapper.checkPostExists(postId);
        return count != null && count > 0;
    }

    @Override
    public PostDetailResponseVO getPostDetail(Integer postId, Integer userId) {
        // 1. 检查帖子是否存在
        Boolean postExists = checkPostExists(postId);
        if (!postExists) {
            throw new BusinessException(ErrorMsg.PARAM_ERROR.getCode(), "帖子不存在");
        }

        // 2. 查询帖子详情
        // 首先，我们需要查询帖子详情的Map对象，这样可以正确获取JSON字符串
        Map<String, Object> postMap = userForumPostMapper.selectPostDetailMap(postId);

        // 创建PostVO对象
        PostDetailResponseVO.PostVO postVO = new PostDetailResponseVO.PostVO();

        // 设置基本字段
        postVO.setPostId((Integer) postMap.get("postId"));
        postVO.setUserId((Integer) postMap.get("userId"));
        postVO.setTitle((String) postMap.get("title"));
        postVO.setContent((String) postMap.get("content"));
        postVO.setCreateTime((String) postMap.get("createTime"));
        postVO.setLikeCount((Integer) postMap.get("likeCount"));
        postVO.setCollectCount((Integer) postMap.get("collectCount"));
        postVO.setPublisherName((String) postMap.get("publisherName"));
        postVO.setPublisherAvatar((String) postMap.get("publisherAvatar"));

        // 处理图片列表
        String imagesJson = (String) postMap.get("images");
        if (imagesJson != null && !imagesJson.isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                List<String> imagesList = objectMapper.readValue(imagesJson,
                        new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                        });
                postVO.setImages(imagesList);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                e.printStackTrace();
                postVO.setImages(new ArrayList<String>());
            }
        } else {
            postVO.setImages(new ArrayList<String>());
        }

        // 3. 查询帖子回复列表
        List<PostDetailResponseVO.ReplyVO> replies = userForumPostReplyMapper.getPostReplies(postId);

        // 4. 组装返回结果
        PostDetailResponseVO postDetailVO = new PostDetailResponseVO();
        postDetailVO.setPost(postVO);
        postDetailVO.setReplies(replies);

        // 5. 检查当前用户是否已点赞和收藏该帖子（如果有userId）
        if (userId != null) {
            // 检查是否已点赞
            Integer likeCount = userForumPostLikeMapper.checkUserLikePost(userId, postId);
            postDetailVO.setIsLiked(likeCount != null && likeCount > 0);

            // 检查是否已收藏
            Integer collectCount = userForumPostFavoriteMapper.checkUserFavoritePost(userId, postId);
            postDetailVO.setIsCollected(collectCount != null && collectCount > 0);
        } else {
            // 如果没有userId，设置为false
            postDetailVO.setIsLiked(false);
            postDetailVO.setIsCollected(false);
        }

        return postDetailVO;
    }
}