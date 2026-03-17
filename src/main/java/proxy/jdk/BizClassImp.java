package proxy.jdk;

public class BizClassImp implements BizClass {
    @Override
    public Object invoke() {
        System.out.println("bizzzzz......");
        return null;
    }
}
