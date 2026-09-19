// ==================== AI配置实体 ====================
package com.baffang.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("ai_customer_service_config")
public class AiCustomerServiceConfig {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String configKey;
    
    private String configValue;
    
    /** 配置类型：0-文本，1-JSON，2-数字 */
    private Integer configType;
    
    private String description;
    
    private Date createTime;
    
    private Date updateTime;
}
