package com.baffang.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "精华设置参数")
public class PostEssenceDTO {
    
    @NotNull(message = "请指定精华状态")
    @Schema(description = "是否精华：0-取消精华，1-设为精华", required = true)
    private Integer isEssence;
}
