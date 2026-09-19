// ==================== 对话详情VO ====================
package com.baffang.admin.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ConversationDetailVO {
    
    private Integer id;
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
    
    private Date createTime;
    private Date endTime;
    private String remark;
    
    /** 参与者列表 */
    private List<ParticipantVO> participants;
}


