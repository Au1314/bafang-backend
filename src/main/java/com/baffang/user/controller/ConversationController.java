package com.baffang.user.controller;

import com.baffang.common.constant.JwtConstants;
import com.baffang.common.result.PageResult;
import com.baffang.common.result.Result;
import com.baffang.common.util.JwtUtil;
import com.baffang.user.dto.ManualServiceConsultationDTO;
import com.baffang.user.dto.SendMessageDTO;
import com.baffang.user.service.ConversationService;
import com.baffang.user.vo.ConversationCreateVO;
import com.baffang.user.vo.ConversationListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对话控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    /**
     * 创建与私桩桩主预约对话
     *
     * @param authorization 请求头中的Authorization令牌
     * @param stationId     私桩ID
     * @return 对话创建结果
     */
    @PostMapping("/charging/private-station/{stationId}/conversation")
    public Result<ConversationCreateVO> createConversationWithStationOwner(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("stationId") Integer stationId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        }

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法创建对话
        ConversationCreateVO conversationCreateVO = conversationService.createConversationWithStationOwner(userId,
                stationId);

        // 4. 返回结果
        Result<ConversationCreateVO> result = Result.success(conversationCreateVO);
        result.setMsg("对话创建成功");
        return result;
    }

    /**
     * 查询用户所有对话消息
     *
     * @param authorization 请求头中的Authorization令牌
     * @param pageNum       页码（默认1）
     * @param pageSize      每页条数（默认20）
     * @return 对话列表结果
     */
    @GetMapping("/conversation/list")
    public Result<PageResult<ConversationListVO>> queryConversationList(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        }

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法查询对话列表
        return conversationService.queryConversationList(userId, pageNum, pageSize);
    }

    /**
     * 发送消息
     *
     * @param authorization  请求头中的Authorization令牌
     * @param conversationId 对话ID
     * @param sendMessageDTO 发送消息请求参数
     * @return 消息发送结果
     */
    @PostMapping("/conversation/{conversationId}")
    public Result<?> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("conversationId") String conversationId,
            @RequestBody SendMessageDTO sendMessageDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        }

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法发送消息
        return conversationService.sendMessage(userId, Integer.parseInt(conversationId), sendMessageDTO);
    }

    /**
     * 查询对话消息列表
     *
     * @param authorization  请求头中的Authorization令牌
     * @param conversationId 对话ID
     * @param pageNum        页码（默认1）
     * @param pageSize       每页条数（默认20）
     * @return 对话消息列表结果
     */
    @GetMapping("/conversation/{conversationId}/message")
    public Result<PageResult<?>> queryConversationMessages(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("conversationId") String conversationId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        }

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法查询对话消息列表
        return conversationService.queryConversationMessages(userId, Integer.parseInt(conversationId), pageNum,
                pageSize);
    }

    /**
     * 结束对话
     *
     * @param authorization  请求头中的Authorization令牌
     * @param conversationId 对话ID
     * @return 结束对话结果
     */
    @DeleteMapping("/conversation/{conversationId}")
    public Result<?> endConversation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable("conversationId") String conversationId) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        }

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法结束对话
        return conversationService.endConversation(userId, Integer.parseInt(conversationId));
    }

    /**
     * 发起人工客服咨询
     *
     * @param authorization   请求头中的Authorization令牌
     * @param consultationDTO 咨询请求参数
     * @return 发起咨询结果
     */
    @PostMapping("/service/manual/conversation")
    public Result<?> initiateManualServiceConsultation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody ManualServiceConsultationDTO consultationDTO) {
        // 1. 从Authorization头中获取令牌（去掉Bearer前缀）
        String token = null;
        if (authorization != null && authorization.startsWith(JwtConstants.TOKEN_PREFIX)) {
            token = authorization.replace(JwtConstants.TOKEN_PREFIX, "");
        }

        // 2. 解析令牌获取用户ID（Integer类型）
        Integer userId = JwtUtil.getUserIdIntFromToken(JwtConstants.SECRET_KEY, token);

        // 3. 调用Service层方法发起人工客服咨询
        return conversationService.initiateManualServiceConsultation(userId, consultationDTO.getContent());
    }
}