package com.baffang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baffang.admin.mapper.SystemNoticeMapper;
import com.baffang.common.entity.SystemNotice;
import com.baffang.user.mapper.UserForumPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 知识检索服务（RAG-lite，真实可运营整改）
 * 深度思考：DeepSeek 不提供 embedding 接口，真正的向量检索在此不可用；
 * 因此采用"关键词检索增强"——从论坛帖与平台公告中检索与用户问题相关的片段，
 * 注入系统提示词，让 AI 回答有据可依。向量库（ES/pgvector）作为演进项。
 *
 * 检索源：①论坛帖子（复用现有关键字搜索）②平台公告（type=0 系统公告）
 * 评分：标题命中 ×3 + 内容命中 ×1，取 TopK 拼装为提示词上下文。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeRetrievalService {

    /** 每条知识片段的内容截断长度（控制提示词体积） */
    private static final int SNIPPET_MAX = 200;
    /** 业务词表：中文无分词器，用领域词直接匹配提取关键词 */
    private static final String[] GLOSSARY = {
            "充电桩", "充电站", "私桩", "公桩", "充电", "预约", "认证", "审核", "积分", "余额",
            "支付", "退款", "紧急", "呼叫", "救援", "互助", "论坛", "帖子", "评论", "举报",
            "申诉", "客服", "通知", "公告", "价格", "电费", "服务费", "会员", "实名",
            "押金", "发票", "优惠券", "折扣", "桩主", "共享", "故障", "续费", "促销"
    };

    private final UserForumPostMapper userForumPostMapper;
    private final SystemNoticeMapper systemNoticeMapper;

    /**
     * 检索与用户消息相关的平台知识片段
     *
     * @param userMessage 用户消息
     * @param topK        返回片段数
     * @return 格式化的上下文文本；无相关内容时返回空串（调用方据此跳过注入）
     */
    public String retrieve(String userMessage, int topK) {
        try {
            if (userMessage == null || userMessage.isBlank()) {
                return "";
            }
            List<String> keywords = extractKeywords(userMessage);
            if (keywords.isEmpty()) {
                return "";
            }

            // 片段容器：key=来源标识（防重复），value=格式化片段
            Map<String, String> snippets = new LinkedHashMap<>();

            // ① 论坛帖子：复用现有关键字搜索（标题或内容 LIKE 首个关键词）
            for (String keyword : keywords) {
                List<Map<String, Object>> posts =
                        userForumPostMapper.getPostList("new", keyword);
                for (Map<String, Object> post : posts) {
                    String id = "post-" + post.get("id");
                    if (snippets.containsKey(id) || snippets.size() >= topK) {
                        continue;
                    }
                    String title = str(post.get("title"));
                    String content = str(post.get("content"));
                    int score = hits(title, keywords) * 3 + hits(content, keywords);
                    if (score > 0) {
                        snippets.put(id, "[论坛帖] " + title + "：" + truncate(content));
                    }
                }
                if (snippets.size() >= topK) {
                    break;
                }
            }

            // ② 平台公告：最近 20 条系统公告，按关键词命中筛选
            List<SystemNotice> notices = systemNoticeMapper.selectList(
                    new LambdaQueryWrapper<SystemNotice>()
                            .eq(SystemNotice::getType, 0)
                            .eq(SystemNotice::getIsDeleted, 0)
                            .orderByDesc(SystemNotice::getCreateTime)
                            .last("limit 20"));
            for (SystemNotice notice : notices) {
                String id = "notice-" + notice.getId();
                if (snippets.containsKey(id) || snippets.size() >= topK) {
                    continue;
                }
                int score = hits(str(notice.getTitle()), keywords) * 3
                        + hits(str(notice.getContent()), keywords);
                if (score > 0) {
                    snippets.put(id, "[平台公告] " + notice.getTitle() + "：" + truncate(notice.getContent()));
                }
            }

            if (snippets.isEmpty()) {
                return "";
            }
            return String.join("\n", new ArrayList<>(snippets.values()).subList(0, Math.min(topK, snippets.size())));
        } catch (Exception e) {
            // 检索失败绝不阻塞聊天主流程
            log.error("知识检索失败，跳过注入: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 关键词提取：① 业务词表命中 ② 按标点/空格切分的长度≥2片段
     */
    private List<String> extractKeywords(String message) {
        List<String> keywords = new ArrayList<>();
        for (String term : GLOSSARY) {
            if (message.contains(term) && !keywords.contains(term)) {
                keywords.add(term);
            }
        }
        // 标点/空格切分，保留长度 2~12 的片段作为补充关键词（最多 3 个）
        for (String seg : message.split("[，。！？、,.!?\\s\\-—（）()\\[\\]]+")) {
            if (seg.length() >= 2 && seg.length() <= 12 && !keywords.contains(seg)) {
                keywords.add(seg);
                if (keywords.size() >= 6) {
                    break;
                }
            }
        }
        return keywords;
    }

    private int hits(String text, List<String> keywords) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int count = 0;
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                count++;
            }
        }
        return count;
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        String plain = text.replaceAll("\\s+", " ").trim();
        return plain.length() <= SNIPPET_MAX ? plain : plain.substring(0, SNIPPET_MAX) + "…";
    }

    private String str(Object v) {
        return v == null ? "" : String.valueOf(v);
    }
}
