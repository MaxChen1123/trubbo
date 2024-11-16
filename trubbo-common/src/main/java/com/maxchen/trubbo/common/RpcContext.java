package com.maxchen.trubbo.common;

import com.maxchen.trubbo.common.URL.URL;
import lombok.Data;

import java.net.URISyntaxException;

@Data
public class RpcContext {
    private static final ThreadLocal<RpcContext> LOCAL = ThreadLocal.withInitial(RpcContext::new);
    private volatile URL url;
    private volatile long requestId;
    private volatile boolean isRequest;
    private volatile boolean isHeartBeat;
    private volatile boolean isOneWay;
    private volatile boolean isAsync;

    //    private String serviceName;
//    private String methodName;
//    private Class<?>[] argsTypes;
//    private Object[] args;
    public void setUrlFromString(String url) throws URISyntaxException {
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
