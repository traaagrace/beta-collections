package SPI;

import java.util.ServiceLoader;

public class TestSPI {
    public static void main(String[] args) {
        ServiceLoader<MyService> services = ServiceLoader.load(MyService.class);
        for (MyService s : services) {
            s.execute();
        }
    }
}
