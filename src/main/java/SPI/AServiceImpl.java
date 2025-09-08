package SPI;

public class AServiceImpl implements MyService {
    @Override
    public void execute() {
        System.out.println("A 服务实现");
    }
}

