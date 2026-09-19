package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * 对话表实体类
 */
@Data
public class Conversation {
    /**
     * 主键
     */
    private Integer id;
    
    /**
     * 对话类型：0-客服对话，1-预约对话，2-紧急呼叫对话，3-V2V 对话，4-普通用户聊天
     */
    private Integer type;
    
    /**
     * 对话状态：1-进行中，2-已完成，3-已关闭
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 逻辑删除标识，0：正常，1：已删除
     */ 
    @TableLogic
    private Integer isDeleted;
}