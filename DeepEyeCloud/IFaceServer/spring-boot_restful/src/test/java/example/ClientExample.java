package example;

// import org.apache.thrift... etc.;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class ClientExample {
    private static final int PORT = 7911;

    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", PORT);
            TBinaryProtocol protocol = new TBinaryProtocol(transport);
            ServiceExample.Client client = new ServiceExample.Client(protocol);
            transport.open();
            BeanExample bean = client.getBean(1, "string");
            transport.close();
            System.out.println(bean.getStringObject());
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}