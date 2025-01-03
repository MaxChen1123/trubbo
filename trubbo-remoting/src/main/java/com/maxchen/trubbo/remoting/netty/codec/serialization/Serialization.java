package com.maxchen.trubbo.remoting.netty.codec.serialization;

public interface Serialization {
    byte[] serialize(Object object) throws Exception;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}
