package proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class InvoketionInteceptorImp implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("InvoketionInteceptorImp.intercept()");
        methodProxy.invokeSuper(o, objects);
        System.out.println("InvoketionInteceptorImp.intercept() end");
        return null;
    }
}
