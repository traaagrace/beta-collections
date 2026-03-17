package proxy.cglib;


public class CGlibProxy {
    public static void main(String[] args) {
        BizClass proxy = (BizClass) ProxyFactory.getProxy(BizClass.class);
        proxy.invoke();
    }
}
