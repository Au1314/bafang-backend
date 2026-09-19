package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "置顶设置参数")
public class PostTopDTO {
    
    @NotNull(message = "请指定置顶状态")
    @Schema(description = "是否置顶：0-取消置顶，1-置顶", required = true)
    private Integer isTop;
}
