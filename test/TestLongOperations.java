import bartosh.client.IClient;
import bartosh.client.TCPClient;
import bartosh.client.UDPClient;
import bartosh.server.Server;

import java.io.IOException;

/**
 * Created by Fedor Bartosh on 16.06.15
 */
public class TestLongOperations {

    private static final int PORT = 2013;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT).start();

        TCPClient tcpClient = new TCPClient("localhost", PORT);
        doTest(tcpClient);
        tcpClient.close();

        UDPClient udpClient = new UDPClient("localhost", PORT);
        doTest(udpClient);
        udpClient.close();

        server.shutdown();
    }

    private static void doTest(IClient client) throws IOException {
        long longBiggerThanMaxInteger = (long) Integer.MAX_VALUE + 1000;
        client.add(longBiggerThanMaxInteger);
        System.out.println("Expected: " + longBiggerThanMaxInteger);
        System.out.println("Real: " + client.get());
        System.out.println();

        client.add(-longBiggerThanMaxInteger);
        System.out.println("Expected: " + 0);
        System.out.println("Real: " + client.get());
        System.out.println();

        long reallyBigLong = Long.MAX_VALUE - 10;
        client.add(reallyBigLong);
        client.add(20);
        client.add(-reallyBigLong);
        System.out.println();


        long reallySmallLong = Long.MIN_VALUE + 10;
        client.add(reallySmallLong);
        client.add(-20);
        client.add(-reallySmallLong);
        System.out.println();
    }
}
