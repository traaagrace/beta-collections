package langchain;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class DeepSeekDemo {

    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-dd429bdff4c343c4b86f8ddd47422132")
                .baseUrl("https://api.deepseek.com/v1/")
                .modelName("deepseek-chat")
                .temperature(0.7)
                .build();

//        String answer = model.generate("用一句话解释 ThreadPoolExecutor 的 execute 流程");

//        System.out.println(answer);
    }
}
