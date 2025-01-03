package com.maxchen.trubbo.rpc.proxy;

import com.maxchen.trubbo.common.configuration.ConfigConstants;
import com.maxchen.trubbo.common.configuration.ConfigurationContext;
import com.maxchen.trubbo.remoting.netty.exchange.Response;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import com.maxchen.trubbo.rpc.protocol.consumer.ConsumerInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ConsumerInvocationHandler implements InvocationHandler {
    private final Invoker invoker;
    private final String serviceName;

    public ConsumerInvocationHandler(Invoker invoker) {
        this.invoker = invoker;
        this.serviceName = invoker.getServiceName();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return invoker.toString();
            } else if ("hashCode".equals(methodName)) {
                return invoker.hashCode();
            }
        } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
            return invoker.equals(args[0]);
        }

        //if the return type is Future, we consider this invocation as an async one
        boolean isAsync = Future.class.isAssignableFrom(method.getReturnType());
        String isOneway = ConfigurationContext
                .getMethodConfigProperty(serviceName, methodName, ConfigConstants.ONE_WAY_KEY, "false");

        ConsumerInvocation inv = ConsumerInvocation.builder()
                .args(args)
                .argsTypes(parameterTypes)
                .serviceName(serviceName)
                .methodName(methodName)
                .isAsync(isAsync)
                .isOneWay(Boolean.parseBoolean(isOneway))
                .build();
        InvocationResult result = invoker.invoke(inv);
        if (result == null) {
            return null;
        }
        if (isAsync) {
            Future<Response> future = result.getFuture();
            return ((CompletableFuture<Response>) future).thenApply(Response::getResult);
        } else {
            Response response = result.get();
            return result.get().getResult();
        }
    }
}
