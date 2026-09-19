package com.baffang.admin.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ParticipantVO {
    
    private Integer participantId;
    
    private Integer participantType;
    
    private String participantTypeName;
    
    private Integer role;
    
    private String roleName;
    
    private String name;
    
    private String avatar;
    
    private Date joinTime;
}
