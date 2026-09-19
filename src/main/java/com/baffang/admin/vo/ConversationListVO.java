// ==================== 对话列表VO ====================
package com.baffang.admin.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ConversationListVO {
    
    private Integer id;
    
    /** 对话状态 */
    private Integer status;
    private String statusName;
    
    /** 用户信息 */
    private Integer userId;
    private String userName;
    private String userPhone;
    private String userAvatar;
    
    /** 客服信息 */
    private Integer adminId;
    private String adminName;
    
    /** 最后一条消息 */
    private String lastMessage;
    private Date lastMessageTime;
    
    /** 未读消息数 */
    private Integer unreadCount;
    
    /** 消息总数 */
    private Integer messageCount;
    
    private Date createTime;
    private Date endTime;
}
