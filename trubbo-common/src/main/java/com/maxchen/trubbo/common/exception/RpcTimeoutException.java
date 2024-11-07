package com.maxchen.trubbo.common.exception;

public class RpcTimeoutException extends RuntimeException {
    public RpcTimeoutException(String message) {
        super(message);
    }
}
