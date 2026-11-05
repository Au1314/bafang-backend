package com.baffang.common.util;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CSV 导出工具
 * 生成带 BOM 的 UTF-8 CSV（Excel 可直接打开不乱码），字段按 RFC 4180 转义。
 */
public class CsvUtil {

    private CsvUtil() {
    }

    /**
     * 构建带 BOM 的 UTF-8 CSV 字节流
     *
     * @param headers 表头
     * @param rows    数据行（与表头列数一致）
     */
    public static byte[] toCsvBytes(List<String> headers, List<List<Object>> rows) {
        StringBuilder sb = new StringBuilder();
        // UTF-8 BOM：Excel 识别编码用
        sb.append('\uFEFF');
        writeLine(sb, headers);
        if (rows != null) {
            for (List<Object> row : rows) {
                writeLine(sb, row);
            }
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void writeLine(StringBuilder sb, List<?> cells) {
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escape(String.valueOf(cells.get(i))));
        }
        sb.append("\r\n");
    }

    /**
     * RFC 4180 转义：含逗号/引号/换行的字段用双引号包裹，内部双引号翻倍
     */
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needQuote = value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needQuote ? "\"" + escaped + "\"" : escaped;
    }
}
