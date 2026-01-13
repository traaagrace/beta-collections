package langchain;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface JavaAssistant {

    @SystemMessage("你是一个资深 Java 并发工程师")
    String answer(@UserMessage String question);
}