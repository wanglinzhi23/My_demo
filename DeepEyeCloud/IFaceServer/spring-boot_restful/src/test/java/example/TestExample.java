package example;

// imports... see last example for correct imports

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestExample {
	public static void main(String[] args) {
		String abc = "sdfsdf/sdfsdfsfd.jpg";
		String fName = abc.split("\\.")[0];
		String[] nameStr = fName.split("/");
		String name = nameStr[nameStr.length - 1];
		System.out.println(name);
	}
    private static final int PORT = 7911;

    @BeforeClass
    @SuppressWarnings({"static-access"})
    public static void startServer() throws URISyntaxException, IOException {
        // Start thrift server in a seperate thread
        new Thread(new ServerExample()).start();
        try {
            // wait for the server start up
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExample() throws TTransportException, TException {
        TTransport transport = new TSocket("localhost", PORT);
        TProtocol protocol = new TBinaryProtocol(transport);
        ServiceExample.Client client = new ServiceExample.Client(protocol);
        transport.open();
        BeanExample bean = client.getBean(1, "string");
        transport.close();
        Assert.assertEquals("OK", bean.getStringObject());
    }
}