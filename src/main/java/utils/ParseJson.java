package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseJson {
    public static void main(String[] args) throws Exception {
        String json = "{\"title\":{\"elements\":[],\"location\":{\"zoneId\":\"0\",\"startIndex\":0,\"endIndex\":0}},\"body\":{\"blocks\":[{\"type\":\"paragraph\",\"paragraph\":{\"elements\":[{\"type\":\"textRun\",\"textRun\":{\"text\":\"booking：边奥北\",\"style\":{},\"location\":{\"zoneId\":\"0\",\"startIndex\":1,\"endIndex\":12}}}],\"location\":{\"zoneId\":\"0\",\"startIndex\":1,\"endIndex\":12},\"lineId\":\"RwSuJ1\"}},{\"type\":\"paragraph\",\"paragraph\":{\"elements\":[{\"type\":\"textRun\",\"textRun\":{\"text\":\"Kylin: 奥北边\",\"style\":{},\"location\":{\"zoneId\":\"0\",\"startIndex\":14,\"endIndex\":24}}}],\"style\":{},\"location\":{\"zoneId\":\"0\",\"startIndex\":13,\"endIndex\":24},\"lineId\":\"MdHDzV\"}},{\"type\":\"paragraph\",\"paragraph\":{\"elements\":[],\"location\":{\"zoneId\":\"0\",\"startIndex\":25,\"endIndex\":25},\"lineId\":\"1O7Bw7\"}}]}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        // 进入 body -> blocks[0] -> paragraph -> elements[0] -> textRun -> text
        String text1 = root.path("body")
                .path("blocks").get(0)
                .path("paragraph")
                .path("elements").get(0)
                .path("textRun")
                .path("text")
                .asText();

        // 第二个内容
        String text2 = root.path("body")
                .path("blocks").get(1)
                .path("paragraph")
                .path("elements").get(0)
                .path("textRun")
                .path("text")
                .asText();
        System.out.println(text1.split("：")[1] + text2.split("：")[0]);
    }
}

