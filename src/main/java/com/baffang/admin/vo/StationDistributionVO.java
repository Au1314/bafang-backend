package com.baffang.admin.vo;

import lombok.Data;
import java.util.List;

@Data
public class StationDistributionVO {
    
    /**
     * 按状态分布
     */
    private List<StatusDistribution> statusDistribution;
    
    /**
     * 按类型分布
     */
    private List<TypeDistribution> typeDistribution;
    
    /**
     * 按电流类型分布
     */
    private List<CurrentTypeDistribution> currentTypeDistribution;
    
    /**
     * 按认证状态分布
     */
    private List<AuthStatusDistribution> authStatusDistribution;
    
    /**
     * 按开放状态分布
     */
    private List<OpenStatusDistribution> openStatusDistribution;

    @Data
    public static class StatusDistribution {
        private Integer status;
        private String statusName;
        private Integer count;
    }

    @Data
    public static class TypeDistribution {
        private Integer type;
        private String typeName;
        private Integer count;
    }

    @Data
    public static class CurrentTypeDistribution {
        private Integer currentType;
        private String currentTypeName;
        private Integer count;
    }

    @Data
    public static class AuthStatusDistribution {
        private Integer authStatus;
        private String authStatusName;
        private Integer count;
    }

    @Data
    public static class OpenStatusDistribution {
        private Integer openStatus;
        private String openStatusName;
        private Integer count;
    }
}
