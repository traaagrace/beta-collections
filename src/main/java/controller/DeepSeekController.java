package controller;

/**
 * @author: aobei.bian
 * @date: 2025/3/23 20:59
 * @description:
 */
import lombok.val;
import org.springframework.web.bind.annotation.*;
import utils.DeepSeekClient;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/chat")
public class DeepSeekController {

    private final DeepSeekClient deepSeekClient;

    public DeepSeekController() {
        // 替换为实际的API Key
        String apiKey = "sk-dd429bdff4c343c4b86f8ddd47422132";
        this.deepSeekClient = new DeepSeekClient(apiKey);
    }

    // 处理用户输入的聊天请求
    @RequestMapping("/testCpu")
    public String askAssistant() throws InterruptedException {
        val strings = new HashSet<String>();
        handleMem(strings);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                System.out.println(finalI);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, String.valueOf(i)).start();
            strings.add(String.valueOf(i));
        }
        return "";
    }

    private void handleMem(HashSet<String> strings) throws InterruptedException {
        Thread.sleep(2000);
        strings.add(String.valueOf(System.currentTimeMillis()));

    }


    // 处理用户输入的聊天请求
    @PostMapping("/ask")
    public String askAssistant(@RequestBody Map<String, String> request) {
        String userInput = request.get("userInput");
        if (userInput == null || userInput.isEmpty()) {
            return "User input cannot be empty.";
        }

        // 构造消息列表
        List<Map<String, String>> messages = Arrays.asList(
                Map.of("role", "system", "content", "你是一个java技术文档总结机器人，要从我给你提供的网址" +
                        "帮我总结spring框架相关的知识。输出内容要是markdown格式内容，标题自己总结出来"),
                Map.of("role", "user", "content", userInput)
        );

        try {
            // 发送请求并获取响应
            return deepSeekClient.sendChatRequest(messages);
        } catch (IOException e) {
            e.printStackTrace();
            return "An error occurred while processing your request.";
        }
    }

    // 处理用户输入的聊天请求
    @GetMapping("/test")
    public String getTest() {
        return "test";
    }
}
