package com.baffang.common.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 时间工具类
 * 用于处理各种时间格式的转换，特别是将时间字符串转换为数据库可接受的DATETIME格式
 */
public class TimeUtil {

    /**
     * 时间格式：HH:mm:ss
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * DateTimeFormatter实例：HH:mm:ss
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    /**
     * DateTimeFormatter实例：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    /**
     * 将时间字符串（如"08:00:00"）转换为当前日期的DATETIME字符串
     * 例如："08:00:00" -> "2026-01-07 08:00:00"
     * 
     * @param timeStr 时间字符串，格式为HH:mm:ss
     * @return 日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     * @throws DateTimeParseException 如果时间字符串格式不正确
     */
    public static String timeStrToCurrentDatetimeStr(String timeStr) {
        LocalTime localTime = LocalTime.parse(timeStr, TIME_FORMATTER);
        LocalDateTime localDateTime = LocalDateTime.now().with(localTime);
        return localDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 将时间字符串（如"08:00:00"）转换为指定日期的DATETIME字符串
     * 
     * @param dateStr 日期字符串，格式为yyyy-MM-dd
     * @param timeStr 时间字符串，格式为HH:mm:ss
     * @return 日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     * @throws DateTimeParseException 如果日期或时间字符串格式不正确
     */
    public static String timeStrToDatetimeStr(String dateStr, String timeStr) {
        String datetimeStr = dateStr + " " + timeStr;
        LocalDateTime localDateTime = LocalDateTime.parse(datetimeStr, DATETIME_FORMATTER);
        return localDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 解析日期时间字符串为LocalDateTime对象
     * 
     * @param datetimeStr 日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     * @return LocalDateTime对象
     * @throws DateTimeParseException 如果日期时间字符串格式不正确
     */
    public static LocalDateTime parseDatetime(String datetimeStr) {
        return LocalDateTime.parse(datetimeStr, DATETIME_FORMATTER);
    }

    /**
     * 解析时间字符串为LocalTime对象
     * 
     * @param timeStr 时间字符串，格式为HH:mm:ss
     * @return LocalTime对象
     * @throws DateTimeParseException 如果时间字符串格式不正确
     */
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    /**
     * 格式化LocalDateTime对象为日期时间字符串
     * 
     * @param localDateTime LocalDateTime对象
     * @return 日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     */
    public static String formatDatetime(LocalDateTime localDateTime) {
        return localDateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 格式化Date对象为日期时间字符串
     * 
     * @param date Date对象
     * @return 日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     */
    public static String formatDatetime(java.util.Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        return formatDatetime(localDateTime);
    }

    /**
     * 格式化LocalTime对象为时间字符串
     * 
     * @param localTime LocalTime对象
     * @return 时间字符串，格式为HH:mm:ss
     */
    public static String formatTime(LocalTime localTime) {
        return localTime.format(TIME_FORMATTER);
    }

    /**
     * 验证时间字符串格式是否正确
     * 
     * @param timeStr 时间字符串
     * @return true表示格式正确，false表示格式不正确
     */
    public static boolean isValidTimeFormat(String timeStr) {
        try {
            LocalTime.parse(timeStr, TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 验证日期时间字符串格式是否正确
     * 
     * @param datetimeStr 日期时间字符串
     * @return true表示格式正确，false表示格式不正确
     */
    public static boolean isValidDatetimeFormat(String datetimeStr) {
        try {
            LocalDateTime.parse(datetimeStr, DATETIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 将日期时间字符串（如"2026-01-07 08:00:00"）转换为时间字符串（如"08:00:00"）
     * 
     * @param datetimeStr 日期时间字符串，格式为yyyy-MM-dd HH:mm:ss
     * @return 时间字符串，格式为HH:mm:ss
     * @throws DateTimeParseException 如果日期时间字符串格式不正确
     */
    public static String datetimeStrToTimeStr(String datetimeStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(datetimeStr, DATETIME_FORMATTER);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime.format(TIME_FORMATTER);
    }
}