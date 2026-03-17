package proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokationHandlerImpl implements InvocationHandler {
    public Object target;
    public InvokationHandlerImpl(Object target) {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("InvokationHandlerImp.invoke()");
        method.invoke(target, args);
        System.out.println("InvokationHandlerImp.invoke() end");
        return null;
    }
}
