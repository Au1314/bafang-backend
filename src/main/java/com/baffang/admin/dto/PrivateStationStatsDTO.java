package com.baffang.admin.dto;

import lombok.Data;

@Data
public class PrivateStationStatsDTO {
    
    private Long total;
    
    private StatusStats statusStats;
    
    private AuthStats authStats;
    
    private TypeStats typeStats;

    @Data
    public static class StatusStats {
        private Long idle;
        private Long reserved;
        private Long charging;
        private Long abnormal;
    }

    @Data
    public static class AuthStats {
        private Long unauth;
        private Long pending;
        private Long approved;
        private Long rejected;
    }

    @Data
    public static class TypeStats {
        private Long normal;
        private Long professional;
    }
}
