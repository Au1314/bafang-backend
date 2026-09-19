// ==================== 消息VO ====================
package com.baffang.admin.vo;

import lombok.Data;
import java.util.Date;

@Data
public class MessageVO {
    
    private Integer id;
    
    private Integer conversationId;
    
    private Integer senderId;
    
    /** 发送者类型：0-普通用户，1-管理员，2-系统 */
    private Integer senderType;
    private String senderTypeName;
    
    /** 发送者名称 */
    private String senderName;
    private String senderAvatar;
    
    private String content;
    
    /** 消息类型：0-文本，1-图片，2-语音，3-系统通知 */
    private Integer messageType;
    private String messageTypeName;
    
    private String extraInfo;
    
    private Date sendTime;
    
    private Integer isRead;
}
