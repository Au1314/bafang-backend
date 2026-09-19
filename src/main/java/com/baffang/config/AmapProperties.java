package com.baffang.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "amap")
public class AmapProperties {
    private String apiKey;
    private String baseUrl;
    private String geocodePath;
    private String regeocodePath;
    private String directionPath;
    private String ipLocatePath;
    private String weatherPath;
    private String sigKey;
    private String output = "JSON";
    
    public String getGeocodeUrl() {
        return baseUrl + geocodePath;
    }
    
    public String getRegeocodeUrl() {
        return baseUrl + regeocodePath;
    }
    
    public String getDirectionUrl() {
        return baseUrl + directionPath;
    }
    
    public String getIpLocateUrl() {
        return baseUrl + ipLocatePath;
    }
    
    public String getWeatherUrl() {
        return baseUrl + weatherPath;
    }
}