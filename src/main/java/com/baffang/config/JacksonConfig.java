package com.baffang.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 手动配置 ObjectMapper Bean，解决注入失败问题
 * 设置全局时区和日期时间格式
 */
@Configuration // 必须加这个注解，让Spring扫描到并注册Bean
public class JacksonConfig {

    // 全局日期时间格式
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // 全局时区
    public static final String TIME_ZONE = "GMT+8";

    // 声明ObjectMapper为Spring Bean，供切面注入
    @Bean
    @Primary // 设置为主要的ObjectMapper，覆盖默认配置
    public ObjectMapper objectMapper() {
        // 创建基础的ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置全局时区为北京时间
        objectMapper.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

        // 设置全局日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT);
        objectMapper.setDateFormat(dateFormat);

        // 配置Java 8时间类型支持
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 配置LocalDateTime的序列化器和反序列化器，指定格式和时区
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        // 注册JavaTimeModule
        objectMapper.registerModule(javaTimeModule);

        // 禁用序列化特性：日期作为时间戳输出
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}