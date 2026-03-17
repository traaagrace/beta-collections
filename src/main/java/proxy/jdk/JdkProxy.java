package proxy.jdk;

/**
* jdk 动态代理几个概念
* 1. 代理类 - 持有被代理对象，通过反射执行被代理逻辑。支持添加增强逻辑。
* 2. 代理对象，通过被代理示例，代理类创建。将代理类与被代理类绑定
* 3. 代理类本质上是被代理类接口的实现类（这也是需要被代理类提供接口的主要原因）
* */

public class JdkProxy {
    public static void main(String[] args) {
        BizClassImp bizClassImp = new BizClassImp();
        BizClass proxy = (BizClass) ProxyFactory.getProxy(bizClassImp);
        proxy.invoke();
    }
}
