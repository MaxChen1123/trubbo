package com.maxchen.trubbo.remoting.exchange;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private long requestId;
    private String serviceName;
    private String methodName; //the service interface method to be invoked
    private Class<?>[] argsTypes;
    private Object[] args;
    private Map<String, Object> attachments;
}
