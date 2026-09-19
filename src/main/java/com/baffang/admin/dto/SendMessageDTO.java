// ==================== 发送消息DTO ====================
package com.baffang.admin.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

import jakarta.validation.constraints.NotBlank;

@Data
public class SendMessageDTO {
    
    /** 消息内容 */
    @NotBlank(message = "消息内容不能为空")
    private String content;
    
    /** 消息类型：0-文本，1-图片，2-语音，3-系统通知 */
    private Integer messageType = 0;
    
    /** 扩展信息（JSON格式，如图片URL等） */
    private String extraInfo;
}
