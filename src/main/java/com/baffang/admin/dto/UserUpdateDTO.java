package com.baffang.admin.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class UserUpdateDTO {
    /**
     * 昵称
     */
    private String name;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 用户类型：0-普通用户，1-VIP用户
     */
    private Integer type;
    
    /**
     * 用户签名
     */
    private String signature;
    
    /**
     * 积分
     */
    private Integer points;
    
    /**
     * 余额
     */
    private java.math.BigDecimal balance;
}
