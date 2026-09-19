package com.baffang.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 登录响应Token DTO
 */
@Data
public class TokenDTO {
    
    private String token;
    
    private String tokenType;
    
    private Long expiresIn;
    
    private AdminInfoDTO adminInfo;
    
    @Data
    public static class AdminInfoDTO {
        
        private Integer id;
        
        private String username;
        
        /**
         * 管理员姓名/昵称
         */
        private String name;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date updateTime;
        
        private Integer type;
        
        private String typeDesc;
    }
}
