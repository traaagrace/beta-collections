package utils;

/**
 * @author: aobei.bian
 * @date: 2025/3/19 22:36
 * @description:
 */
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class HttpUtil {
    private final RestTemplate restTemplate = new RestTemplate();
    public String TestA = "p2p";
    /**
     * 发送 GET 请求
     * @param url 请求地址
     * @param responseType 响应类型
     * @return 响应数据
     */
    public <T> T get(String url, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.getForEntity(url, responseType);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发送带 Header 的 POST 请求
     * @param url 请求地址
     * @param requestBody 请求体
     * @param headers 请求头
     * @param responseType 响应类型
     * @return 响应数据
     */
    public <T> T post(String url, Object requestBody, Map<String, String> headers, Class<T> responseType) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            headers.forEach(httpHeaders::set);

            HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
