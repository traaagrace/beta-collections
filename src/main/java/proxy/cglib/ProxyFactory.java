package proxy.cglib;

import net.sf.cglib.proxy.Enhancer;

public class ProxyFactory {
    public static Object getProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(clazz.getClassLoader());
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new InvoketionInteceptorImp());
        return enhancer.create();
    }
}
