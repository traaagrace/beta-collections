package utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;// 新增版依次插入，新版本会覆盖旧版本相同key的配置

public class Linkhashmp {

    static final Map<String, Map<String, String>> SCENE_CONFIG = new LinkedHashMap<>() {{
        put("123", Map.of("hotelRoomInfoUpBreakfaster", "layBreakfast"));
        put("124", Map.of("hotelRoomInfoUpBreakfaster", "layBreakfast2"));
        put("125", Map.of("hotel", "ts"));
        put("126", Map.of("ls", "ts", "hotel", "ts2"));
    }};

    public static void main(String[] args) {
        Map<String, String> mergedConfig = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : SCENE_CONFIG.entrySet()) {
            if (true) {
                mergedConfig.putAll(entry.getValue());
            }
        }
        System.out.println(mergedConfig);
    }
}
