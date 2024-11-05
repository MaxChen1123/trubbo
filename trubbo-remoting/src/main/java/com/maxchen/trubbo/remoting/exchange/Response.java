package com.maxchen.trubbo.remoting.exchange;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private long requestId;
    private String serviceName;
    private Class<?> returnType;
    private String methodName;
    private Object result;
    private boolean hasException;
    private Throwable exception;
}
