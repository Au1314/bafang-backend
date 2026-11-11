package com.baffang.common.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 阿里云短信实现（手写签名协议，零 SDK 依赖）
 * 配置：sms.provider=aliyun + ALIYUN_SMS_ACCESS_KEY_ID/ALIYUN_SMS_ACCESS_KEY_SECRET/
 * ALIYUN_SMS_SIGN_NAME/ALIYUN_SMS_TEMPLATE_CODE 环境变量齐全时自动启用。
 * 采用阿里云短信传统版 HTTP 协议：HMAC-SHA1 签名 + GET 调用。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sms.provider", havingValue = "aliyun")
public class AliyunSmsSender implements SmsSender {

    private final String accessKeyId;
    private final String accessKeySecret;
    private final String signName;
    private final String templateCode;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AliyunSmsSender(
            @org.springframework.beans.factory.annotation.Value("${sms.aliyun.access-key-id:}") String accessKeyId,
            @org.springframework.beans.factory.annotation.Value("${sms.aliyun.access-key-secret:}") String accessKeySecret,
            @org.springframework.beans.factory.annotation.Value("${sms.aliyun.sign-name:}") String signName,
            @org.springframework.beans.factory.annotation.Value("${sms.aliyun.template-code:}") String templateCode) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.signName = signName;
        this.templateCode = templateCode;
    }

    @Override
    public boolean enabled() {
        return !accessKeyId.isBlank() && !accessKeySecret.isBlank()
                && !signName.isBlank() && !templateCode.isBlank();
    }

    @Override
    public boolean send(String phone, String templateCodeParam, String templateParam) {
        String tpl = (templateCodeParam == null || templateCodeParam.isBlank())
                ? templateCode : templateCodeParam;
        try {
            TreeMap<String, String> params = new TreeMap<>();
            params.put("AccessKeyId", accessKeyId);
            params.put("Action", "SendSms");
            params.put("Format", "JSON");
            params.put("PhoneNumbers", phone);
            params.put("RegionId", "cn-hangzhou");
            params.put("SignName", signName);
            params.put("SignatureMethod", "HMAC-SHA1");
            params.put("SignatureNonce", UUID.randomUUID().toString());
            params.put("SignatureVersion", "1.0");
            params.put("TemplateCode", tpl);
            params.put("TemplateParam", templateParam);
            params.put("Timestamp", iso8601());
            params.put("Version", "2017-05-25");

            String canonicalQuery = buildQuery(params);
            String stringToSign = "GET&" + percentEncode("/") + "&"
                    + percentEncode(canonicalQuery);
            String signature = hmacSha1(stringToSign, accessKeySecret + "&");
            params.put("Signature", signature);

            String finalQuery = buildQuery(params);
            HttpResponse<String> resp = httpClient.send(
                    HttpRequest.newBuilder(URI.create("https://dysmsapi.aliyuncs.com/?" + finalQuery)).GET().build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = resp.body();
            boolean ok = body.contains("\"Code\":\"OK\"");
            if (!ok) {
                log.warn("阿里云短信发送失败: phone={}, resp={}", phone, body);
            }
            return ok;
        } catch (Exception e) {
            log.error("阿里云短信发送异常: phone={}", phone, e);
            return false;
        }
    }

    /** 排序拼接 k=percentEncode(v)&...（阿里云签名规范） */
    private String buildQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e : new TreeMap<>(params).entrySet()) {
            if (!first) sb.append('&');
            sb.append(percentEncode(e.getKey())).append('=').append(percentEncode(e.getValue()));
            first = false;
        }
        return sb.toString();
    }

    // ==================== 阿里云签名协议 ====================

    // ==================== 阿里云签名协议 ====================

    private String iso8601() {
        return java.time.ZonedDateTime.now(java.time.ZoneId.of("UTC"))
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    private String percentEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    private String hmacSha1(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
