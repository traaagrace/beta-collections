package SPI;

public class BServiceImpl implements MyService {
    @Override
    public void execute() {
        System.out.println("B 服务实现");
    }
}
