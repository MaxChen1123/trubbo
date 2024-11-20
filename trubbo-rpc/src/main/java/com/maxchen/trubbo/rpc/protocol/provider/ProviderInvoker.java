package com.maxchen.trubbo.rpc.protocol.provider;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.netty.exchange.Response;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ProviderInvoker implements Invoker {
    private String serviceName;
    private Object service;

    public ProviderInvoker(String serviceName) {
        this.serviceName = serviceName;
        try {
            Class<?> aClass = Class.forName(serviceName);
            service = aClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        if (invocation instanceof ProviderInvocation providerInvocation) {
            String serviceName = providerInvocation.getServiceName();
            try {
                Class<?> aClass = Class.forName(serviceName + "Impl");
                Method method = aClass.getMethod(providerInvocation.getMethodName(), providerInvocation.getArgsTypes());
                Object invoke = method.invoke(service, providerInvocation.getArgs());
                if (invoke instanceof CompletableFuture<?> c) {
                    invoke = c.get();
                }
                Response build = Response.builder()
                        .result(invoke)
                        .returnType(invoke == null ? null : invoke.getClass())
                        .requestId(RpcContext.getContext().getRequestId())
                        .build();
                log.info("provider response: {}", build);
                return new ProviderInvocationResult(build);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        log.warn("not support invocation type");
        return null;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }
}
