package com.maxchen.trubbo.rpc.proxy;

import com.maxchen.trubbo.rpc.protocol.api.Invoker;

import java.lang.reflect.Proxy;

public class JdkProxyFactory {
    public static <T> T getProxy(Class<T> interfaces, Invoker invoker) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaces}, new ConsumerInvocationHandler(invoker));
    }
}
