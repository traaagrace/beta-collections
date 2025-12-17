package utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DataTest {
    public static void main(String[] args) {
        String checkOutTime = "12:00";
        String delayCheckoutTime = "14:00";

        LocalTime time = LocalTime.parse(checkOutTime);
        LocalTime delayTime = LocalTime.parse(delayCheckoutTime);

        if (!delayTime.isBefore(time)) {
            System.out.println(delayCheckoutTime);
        }


        // ----
        String checkInTime = "12:00";
        int earlyCheckinTime = 2;

        LocalTime time1 = LocalTime.parse(checkInTime);
        LocalTime earlyTime = time1.minusMinutes(earlyCheckinTime);
        System.out.println(earlyTime);

        String userId = "ou_94efa97067d732c0ef058b71328c8e39";
        String name = "边奥北";
        String format = String.format("今日已处理工单%s个，待处理工单%s个，处于第%s位", "a", "b", "c");

        String result = String.format(
                "{\"text\":\"<at user_id=\\\\\"%s\\\\\">%s</at> ，<at user_id=\\\\\"%s\\\\\">%s</at>，%s\"}",
                userId, name, userId, name, format
        );

        System.out.println(result);

    }
}
