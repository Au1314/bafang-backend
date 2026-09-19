// ==================== 转接对话DTO ====================
package com.baffang.admin.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class TransferConversationDTO {
    
    /** 目标客服ID */
    @NotNull(message = "目标客服ID不能为空")
    private Integer targetAdminId;
    
    /** 转接原因 */
    private String reason;
}
