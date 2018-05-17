package example;

import org.apache.thrift.TException;

import java.nio.ByteBuffer;

public class ServiceExampleImpl implements ServiceExample.Iface {
    @Override
    public BeanExample getBean(int anArg, String anOther) throws TException {
        return new BeanExample(true, (byte) 2, (short) 3, 4, 5, 6.0,
                "OK", ByteBuffer.wrap(new byte[]{3, 1, 4}));
    }
}