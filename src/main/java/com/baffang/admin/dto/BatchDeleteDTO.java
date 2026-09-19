package com.baffang.admin.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BatchDeleteDTO {
    @NotEmpty(message = "用户ID列表不能为空")
    private List<Integer> ids;


    
}
