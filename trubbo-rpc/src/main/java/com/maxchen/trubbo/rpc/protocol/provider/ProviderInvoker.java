package com.maxchen.trubbo.rpc.protocol.provider;

import com.maxchen.trubbo.common.RpcContext;
import com.maxchen.trubbo.remoting.netty.exchange.Response;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import com.maxchen.trubbo.rpc.protocol.api.InvocationResult;
import com.maxchen.trubbo.rpc.protocol.api.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ProviderInvoker implements Invoker {
    private String serviceName;
    private Object service;
    private Class<?> serviceClass;

    public ProviderInvoker(String serviceName) {
        this.serviceName = serviceName;
        try {
            this.serviceClass = Class.forName(serviceName + "Impl");
            service = serviceClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public ProviderInvoker(String serviceName, String implName) {
        this.serviceName = serviceName;
        try {
            this.serviceClass = Class.forName(implName);
            service = serviceClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public InvocationResult invoke(Invocation invocation) {
        if (invocation instanceof ProviderInvocation providerInvocation) {
            String serviceName = providerInvocation.getServiceName();
            try {
                Method method = serviceClass.getMethod(providerInvocation.getMethodName(), providerInvocation.getArgsTypes());
                Object invoke = null;

                try {
                    invoke = method.invoke(service, providerInvocation.getArgs());
                } catch (InvocationTargetException e) {
                    if (RpcContext.getContext().isOneWay()) {
                        return null;
                    }
                    Response build = Response.builder()
                            .exception(e.getTargetException())
                            .isException(true)
                            .requestId(RpcContext.getContext().getRequestId())
                            .build();
                    log.info("provider response: {}", build);
                    return new ProviderInvocationResult(build);
                }

                if (RpcContext.getContext().isOneWay()) {
                    log.info("oneway request, do not need response");
                    return null;
                }

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
