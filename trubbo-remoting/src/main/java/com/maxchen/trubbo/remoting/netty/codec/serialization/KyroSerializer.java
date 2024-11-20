package com.maxchen.trubbo.remoting.netty.codec.serialization;

public class KyroSerializer implements Serialization {
    @Override
    public byte[] serialize(Object object) throws Exception {
        return KryoUtil.writeObjectToByteArray(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return KryoUtil.readObjectFromByteArray(bytes, clazz);
    }
}
