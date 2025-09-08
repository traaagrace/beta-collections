package utils;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.time.DateFormatUtils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class YourKitTest {
    public static void main(String[] args) throws InterruptedException {
        String input = "{\"text\":\"\"}";
        String input1 = "{\"text\":\"我要创建一个话题\"}";
        String input2 = "{\"text\":\"我要创建一个话题\"}";
        String input3 = "{\"text\":\"我要创建一个话题\"}";
        ArrayList<String> strings = new ArrayList<>();
        strings.add( input);
        strings.add( input1);
        strings.add( input2);
        strings.add( input3);
        StringBuilder content = new StringBuilder();
        for (String string : strings) {
            // 提取双引号之间的内容
            int end = string.lastIndexOf("\"");
            String text = string.substring(9, end);
            content.append("13434").append(":").append(text).append("\n");
        }
        System.out.println(content.toString());

        long timestampInSeconds = 1689600000;
        Date date = new Date(timestampInSeconds * 1000); // 转为毫秒

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);

        String ids[] = new String[5];
        ids[0] = "1";
        ids[1] = "2";
        ids[2] = "3";
        ids[3] = "4";
        ids[4] = "5";
        System.out.println(ids.toString());

    }
}
