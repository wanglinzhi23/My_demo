package example;

// import org.apache.thrift... etc.;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class ServerExample implements Runnable {
    private static final int PORT = 7911;

    public static void main(String[] args) {
        new Thread(new ServerExample()).run();
    }

    @Override
    public void run() {
        try {
            TServerSocket serverTransport = new TServerSocket(PORT);
            ServiceExample.Processor processor = new ServiceExample.Processor(new ServiceExampleImpl());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.println("Starting server on port " + PORT);
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}