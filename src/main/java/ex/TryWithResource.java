package ex;

import java.io.InputStreamReader;
import java.nio.Buffer;

public class TryWithResource {
    public static void main(String[] args) {
        // closeable()

        try (InputStreamReader reader = new InputStreamReader(System.in);) {
            int read = reader.read(new char[10]);
            System.out.println("read" + read);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
