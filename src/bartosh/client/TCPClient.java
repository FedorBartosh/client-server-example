package bartosh.client;

import bartosh.common.Protocol;
import bartosh.common.Utils;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * Not thread-safe
 *
 * Created by Fedor Bartosh on 11.06.15
 */
public class TCPClient extends AbstractClient {

    private final Socket socket;
    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;

    public TCPClient(String host, int port) throws IOException {
        socket = new Socket(host, port);

        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void add(int... numbers) throws IOException {
        for (int number : numbers) {
            dataOutputStream.writeByte(Protocol.ADD_COMMAND);
            dataOutputStream.writeInt(number);
            dataOutputStream.flush();

            readResponseAndPrint(Integer.toString(number));
        }
    }

    private void readResponseAndPrint(String number) throws IOException {
        byte response = dataInputStream.readByte();
        if (response == Protocol.RESPONSE_OVERFLOW) {
            System.out.println(number + " would overflow server's sum");
        }
    }

    @Override
    public void add(long... numbers) throws IOException {
        for (long number : numbers) {
            dataOutputStream.writeByte(Protocol.ADD_LONG_COMMAND);
            dataOutputStream.writeLong(number);
            dataOutputStream.flush();

            readResponseAndPrint(Long.toString(number));
        }
    }

    @Override
    public long get() throws IOException {
        dataOutputStream.writeByte(Protocol.GET_COMMAND);
        dataOutputStream.flush();

        return dataInputStream.readLong();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public void close() throws IOException {
        dataOutputStream.close();
        dataInputStream.close();
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage:");
            System.out.println("    1) add numbers: java bartosh.client.TCPClient <host> <port> 1 2 3 4");
            System.out.println("    2) get current: java bartosh.client.TCPClient <host> <port>");
            System.out.println();

        } else {
            String host = args[0];
            int port = Utils.parsePort(args[1]);

            if (args.length > 2) {
                int[] ints = parseNumbers(args);

                try (TCPClient client = new TCPClient(host, port)) {
                    client.add(ints);
                    System.out.println("Added: " + Arrays.toString(ints));
                }

            } else {
                try (TCPClient client = new TCPClient(host, port)) {
                    long result = client.get();
                    System.out.println("Sum: " + result);
                }
            }
        }
    }
}
