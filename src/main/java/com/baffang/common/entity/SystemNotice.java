package com.baffang.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统通知实体
 */
@Data
@TableName("system_notice")
public class SystemNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 接收用户ID，NULL表示全体用户 */
    private Integer userId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 通知类型：0-系统公告，1-充电通知，2-预约通知，3-V2V通知，4-紧急呼叫通知，5-订单通知，6-审核通知 */
    private Integer type;

    /** 关联业务ID */
    private Integer relatedId;

    /** 关联业务类型：0-订单，1-预约，2-V2V呼叫，3-紧急呼叫，4-帖子，5-评价 */
    private Integer relatedType;

    /** 是否已读：0-未读，1-已读 */
    private Integer isRead;

    /** 创建时间 */
    private Date createTime;

    /** 阅读时间 */
    private Date readTime;

    /** 0：正常，1：已删除 */
    @TableLogic
    private Integer isDeleted;
}
