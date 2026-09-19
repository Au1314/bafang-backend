// ==================== 待接入对话VO ====================
package com.baffang.admin.vo;

import lombok.Data;
import java.util.Date;

@Data
public class PendingConversationVO {
    
    private Integer id;
    
    /** 用户信息 */
    private Integer userId;
    private String userName;
    private String userPhone;
    private String userAvatar;
    
    /** 等待时长（秒） */
    private Long waitingSeconds;
    
    /** 最后一条消息 */
    private String lastMessage;
    
    /** 消息数量 */
    private Integer messageCount;
    
    private Date createTime;
}
