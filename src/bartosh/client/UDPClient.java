package bartosh.client;

import bartosh.common.Protocol;
import bartosh.common.Utils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Not thread-safe
 *
 * Created by Fedor Bartosh on 12.06.15
 */
public class UDPClient extends AbstractClient {

    private final InetAddress address;
    private int port;
    private DatagramSocket socket = new DatagramSocket();

    public UDPClient(String host, int port) throws SocketException, UnknownHostException {
        this.port = port;
        address = InetAddress.getByName(host);
    }

    @Override
    public void add(int... numbers) throws IOException {
        for (int number : numbers) {
            ByteBuffer buf = ByteBuffer.allocate(5);
            buf.put(Protocol.ADD_COMMAND);
            buf.putInt(number);

            DatagramPacket packet = new DatagramPacket(buf.array(), buf.array().length, address, port);
            socket.send(packet);

            readResponseAndPrint(Integer.toString(number));
        }
    }

    private void readResponseAndPrint(String number) throws IOException {
        DatagramPacket packet;
        byte[] responseBuf = new byte[1];
        packet = new DatagramPacket(responseBuf, responseBuf.length);
        socket.receive(packet);

        if (packet.getData()[0] == Protocol.RESPONSE_OVERFLOW) {
            System.out.println(number + " would overflow server's sum");
        }
    }

    @Override
    public void add(long... numbers) throws IOException {
        for (long number : numbers) {
            ByteBuffer buf = ByteBuffer.allocate(9);
            buf.put(Protocol.ADD_LONG_COMMAND);
            buf.putLong(number);

            DatagramPacket packet = new DatagramPacket(buf.array(), buf.array().length, address, port);
            socket.send(packet);

            readResponseAndPrint(Long.toString(number));
        }
    }

    @Override
    public long get() throws IOException {
        byte[] buf = new byte[] { Protocol.GET_COMMAND};
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        buf = new byte[8];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        ByteBuffer result = ByteBuffer.wrap(packet.getData());
        return result.getLong();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage:");
            System.out.println("    1) add numbers: java bartosh.client.UDPClient <host> <port> 1 2 3 4");
            System.out.println("    2) get current: java bartosh.client.UDPClient <host> <port>");
            System.out.println();

        } else {
            String host = args[0];
            int port = Utils.parsePort(args[1]);

            if (args.length > 2) {
                int[] ints = parseNumbers(args);

                try (UDPClient client = new UDPClient(host, port)) {
                    client.add(ints);
                    System.out.println("Added: " + Arrays.toString(ints));
                }

            } else {
                try (UDPClient client = new UDPClient(host, port)) {
                    long result = client.get();
                    System.out.println("Sum: " + result);
                }
            }
        }
    }
}
