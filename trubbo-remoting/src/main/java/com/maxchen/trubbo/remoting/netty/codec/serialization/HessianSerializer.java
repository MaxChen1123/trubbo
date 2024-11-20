package com.maxchen.trubbo.remoting.netty.codec.serialization;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HessianSerializer implements Serialization {

    @Override
    public byte[] serialize(Object object) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        HessianOutput hessianOutput = new HessianOutput(os);

        hessianOutput.writeObject(object);

        return os.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);

        HessianInput hessianInput = new HessianInput(is);

        return (T) hessianInput.readObject(clazz);
    }

}
