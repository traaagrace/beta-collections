package utils.java;

import java.util.ArrayList;
import java.util.List;

public class TestStream {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        List<String> strings1 = List.of("1", "2");

        strings1.forEach(a -> {
            strings.add(a);
        });
        System.out.println(strings);
    }
}
