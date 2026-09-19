package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.entity.ForumPost;
import com.baffang.common.entity.PostReply;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.FavoritePostDTO;
import com.baffang.user.dto.LikePostDTO;
import com.baffang.user.dto.PublishPostDTO;
import com.baffang.user.dto.ReplyPostDTO;
import com.baffang.user.service.ForumPostFavoriteService;
import com.baffang.user.service.ForumPostReplyService;
import com.baffang.user.service.ForumPostService;
import com.baffang.user.vo.ForumPostListVO;
import com.baffang.user.vo.PostDetailResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端帖子控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class ForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    @Autowired
    private ForumPostReplyService forumPostReplyService;

    @Autowired
    private ForumPostFavoriteService forumPostFavoriteService;

    /**
     * 发布帖子接口
     * 
     * @param authorization  请求头中的Authorization令牌
     * @param publishPostDTO 发布帖子请求参数
     * @return 发布结果
     */
    @PostMapping("/forum/post/publish")
    public Result<?> publishPost(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody PublishPostDTO publishPostDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 将DTO转换为实体
        ForumPost forumPost = new ForumPost();
        forumPost.setUserId(userId);
        forumPost.setTitle(publishPostDTO.getTitle());
        forumPost.setContent(publishPostDTO.getContent());

        // 处理图片列表，转换为JSON格式
        if (publishPostDTO.getImages() != null && !publishPostDTO.getImages().isEmpty()) {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            try {
                forumPost.setImages(objectMapper.writeValueAsString(publishPostDTO.getImages()));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new com.baffang.common.exception.BusinessException(
                        com.baffang.common.enums.ErrorMsg.PARAM_ERROR.getCode(), "图片列表转换失败");
            }
        }

        // 4. 调用Service层方法发布帖子
        Integer postId = forumPostService.publishPost(forumPost);

        // 5. 构建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", postId);

        // 6. 返回结果
        Result<Map<String, Object>> result = Result.success(data);
        result.setMsg("发布成功");
        return result;
    }

    /**
     * 获取帖子列表接口
     * 
     * @param authorization 请求头中的Authorization令牌（可选）
     * @param page          页码，默认1
     * @param size          每页条数，默认10
     * @param sort          排序方式：new=最新发布 /hot=热门（即点赞数），默认new
     * @param keyword       关键字搜索
     * @return 帖子列表
     */
    @GetMapping("/forum/post/list")
    public Result<?> getPostList(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "keyword", required = false) String keyword) {
        // 调用Service层方法获取帖子列表
        PageResult<ForumPostListVO> pageResult = forumPostService.getPostList(page, size, sort, keyword);

        // 返回结果
        Result<PageResult<ForumPostListVO>> result = Result.success(pageResult);
        result.setMsg("查询成功");
        return result;
    }

    /**
     * 回复帖子或回复接口
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param replyPostDTO  回复帖子请求参数
     * @return 回复结果
     */
    @PostMapping("/forum/post/reply")
    public Result<?> replyToPost(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody ReplyPostDTO replyPostDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 将DTO转换为实体
        PostReply postReply = new PostReply();
        postReply.setPostId(replyPostDTO.getPostId());
        postReply.setUserId(userId);
        postReply.setParentId(replyPostDTO.getParentId());
        postReply.setReplyToUserId(replyPostDTO.getTargetUserId());
        postReply.setContent(replyPostDTO.getContent());

        // 4. 调用Service层方法回复帖子
        forumPostReplyService.replyToPostOrReply(postReply);

        // 5. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("回复成功");
        return result;
    }

    /**
     * 点赞帖子接口
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param likePostDTO   点赞帖子请求参数
     * @return 点赞结果
     */
    @PostMapping("/forum/post/like")
    public Result<?> likePost(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody LikePostDTO likePostDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法点赞帖子
        Integer likeCount = forumPostService.likePost(userId, likePostDTO.getPostId());

        // 4. 构建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("likeCount", likeCount);

        // 5. 返回结果
        Result<Map<String, Object>> result = Result.success(data);
        result.setMsg("点赞成功");
        return result;
    }

    /**
     * 取消点赞帖子接口
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param likePostDTO   取消点赞帖子请求参数
     * @return 取消点赞结果
     */
    @DeleteMapping("/forum/post/like")
    public Result<?> cancelLikePost(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody LikePostDTO likePostDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法取消点赞帖子
        Integer likeCount = forumPostService.cancelLikePost(userId, likePostDTO.getPostId());

        // 4. 构建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("likeCount", likeCount);

        // 5. 返回结果
        Result<Map<String, Object>> result = Result.success(data);
        result.setMsg("取消点赞成功");
        return result;
    }

    /**
     * 收藏帖子接口
     * 
     * @param authorization   请求头中的Authorization令牌
     * @param favoritePostDTO 收藏帖子请求参数
     * @return 收藏结果
     */
    @PostMapping("/forum/post/favorite")
    public Result<?> favoritePost(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody FavoritePostDTO favoritePostDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法收藏帖子
        forumPostFavoriteService.favoritePost(userId, favoritePostDTO.getPostId());

        // 4. 返回结果
        Result<?> result = Result.success(null);
        result.setMsg("收藏成功");
        return result;
    }

    /**
     * 取消收藏帖子接口
     * 
     * @param authorization   请求头中的Authorization令牌
     * @param favoritePostDTO 取消收藏帖子请求参数
     * @return 取消收藏结果
     */
    @DeleteMapping("/forum/post/favorite")
    public Result<?> cancelFavoritePost(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody FavoritePostDTO favoritePostDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法取消收藏帖子
        forumPostFavoriteService.cancelFavoritePost(userId, favoritePostDTO.getPostId());

        // 4. 返回结果
        Result<?> result = Result.success(new Object());
        result.setMsg("已取消收藏");
        return result;
    }

    /**
     * 获取用户收藏帖子列表接口
     * 
     * @param authorization 请求头中的Authorization令牌
     * @param page          页码，默认1
     * @param size          每页条数，默认10
     * @return 用户收藏帖子列表
     */
    @GetMapping("/forum/post/my-favorites")
    public Result<?> getUserFavoritePosts(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法获取用户收藏的帖子列表
        PageResult<ForumPostListVO> pageResult = forumPostFavoriteService.getUserFavoritePosts(userId, page, size);

        // 4. 返回结果
        Result<PageResult<ForumPostListVO>> result = Result.success(pageResult);
        result.setMsg("success");
        return result;
    }

    /**
     * 获取帖子详情接口
     * 
     * @param authorization 请求头中的Authorization令牌（可选）
     * @param postId        帖子ID
     * @return 帖子详情（含回复列表）
     */
    @GetMapping("/forum/post/detail")
    public Result<?> getPostDetail(@RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "post_id") Integer postId) {
        // 1. 获取当前用户ID（如果有Authorization头）
        Integer userId = null;
        if (authorization != null && !authorization.isEmpty()) {
            String token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
            userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);
        }

        // 2. 调用Service层方法获取帖子详情
        PostDetailResponseVO postDetailVO = forumPostService.getPostDetail(postId, userId);

        // 3. 返回结果
        Result<PostDetailResponseVO> result = Result.success(postDetailVO);
        result.setMsg("success");
        return result;
    }
}