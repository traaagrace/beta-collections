package utils;

/**
 * @author: aobei.bian
 * @date: 2025/3/19 23:06
 * @description:
 */
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.ObsidianService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class DeepSeekClient {

    private static final String BASE_URL = "https://api.deepseek.com/v1/chat/completions";
    private final String apiKey;

    public ObsidianService obsidianService = new ObsidianService();

    public DeepSeekClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 发送聊天消息并返回响应内容
     * @param messages 消息列表
     * @return 返回聊天响应内容
     * @throws IOException 请求异常
     */
    public String sendChatRequest(List<Map<String, String>> messages) throws IOException {
        // 构建请求体
        JSONObject requestBody = new JSONObject()
                .put("model", "deepseek-chat")
                .put("messages", new JSONArray(messages))
                .put("stream", false);

        // 发送请求并获取响应
        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // 写入请求体
        connection.getOutputStream().write(requestBody.toString().getBytes(StandardCharsets.UTF_8));

        // 处理响应
        if (connection.getResponseCode() == 200) {
            String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            obsidianService.addNote(Map.of("content", response));
            JSONObject responseJson = new JSONObject(response);
            String content = responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            obsidianService.addNote(Map.of("content", content));
            return content;
        } else {
            throw new IOException("Request failed with status code " + connection.getResponseCode());
        }
    }
}
