// ==================== 对话查询DTO ====================
package com.baffang.admin.dto;

import lombok.Data;

@Data
public class ConversationQueryDTO {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    /** 对话状态：1-进行中，2-已完成，3-已关闭 */
    private Integer status;
    
    /** 用户ID */
    private Integer userId;
    
    /** 客服ID */
    private Integer adminId;
    
    /** 开始时间 */
    private String startTime;
    
    /** 结束时间 */
    private String endTime;
    
    /** 关键词（搜索消息内容） */
    private String keyword;
}
