package com.maxchen.trubbo.common;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Data;

import java.net.URISyntaxException;

@Data
public class RpcContext {
    private static final TransmittableThreadLocal<RpcContext> LOCAL = new TransmittableThreadLocal<>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };
    private URL url;
    private long requestId;
    private boolean isRequest;
    private boolean isHeartBeat;
    private boolean isAsync;

    //    private String serviceName;
//    private String methodName;
//    private Class<?>[] argsTypes;
//    private Object[] args;
    public void setUrl(String url) throws URISyntaxException {
        this.url = new URL(url);
    }

    public static RpcContext getContext() {
        return LOCAL.get();
    }

    public static void removeContext() {
        LOCAL.remove();
    }

    public static void setContext(RpcContext context) {
        LOCAL.set(context);
    }

    @Deprecated
    public static RpcContext getOrCreateContext() {
        RpcContext context = getContext();
        if (context == null) {
            context = new RpcContext();
            setContext(context);
        }
        return context;
    }
}
