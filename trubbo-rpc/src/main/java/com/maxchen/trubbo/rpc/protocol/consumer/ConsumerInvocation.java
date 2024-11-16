package com.maxchen.trubbo.rpc.protocol.consumer;

import com.maxchen.trubbo.common.URL.URL;
import com.maxchen.trubbo.remoting.exchange.Request;
import com.maxchen.trubbo.rpc.protocol.api.Invocation;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;


@Data
@Builder
public class ConsumerInvocation implements Invocation {
    private static final AtomicLong REQUEST_ID_GEN = new AtomicLong(0);
    private URL url;
    private String serviceName;
    private String methodName;
    private Object[] args;
    private Class<?>[] argsTypes;
    private boolean isAsync;
    private boolean isOneWay;

    @Override
    public Request toRequest() {
        return Request.builder()
                .requestId(REQUEST_ID_GEN.getAndIncrement())
                .args(args)
                .argsTypes(argsTypes)
                .serviceName(serviceName)
                .methodName(methodName)
                .attachments(new HashMap<>(8))
                .build();
    }
}
