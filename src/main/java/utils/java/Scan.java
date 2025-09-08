package utils.java;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Locale;

public class Scan {
    public static void main(String[] args) throws IOException {

        Scanner s = null;
        double sum = 0;

        try {
            s = new Scanner(new BufferedReader(new FileReader("usnumbers.txt")));
            s.useLocale(Locale.US); // 设置美国区域格式

            while (s.hasNext()) {
                if (s.hasNextDouble()) {
                    sum += s.nextDouble(); // 累加 double 类型值
                } else {
                    s.next(); // 如果不是数字，跳过
                }
            }
        } finally {
            s.close();
        }

        System.out.println(sum);
    }
}
