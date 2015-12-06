package bartosh.server;

import bartosh.common.Utils;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Fedor Bartosh on 11.06.15
 */
public class Server {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1000));

    private AtomicLong sum = new AtomicLong();

    private int port;
    private TCPAcceptor tcpAcceptor;
    private UDPAcceptor udpAcceptor;

    private volatile boolean stop = false;

    public Server(int port) {
        this.port = port;
    }

    public Server start() throws IOException {
        System.out.println("Starting server on port " + port);

        tcpAcceptor = new TCPAcceptor(this);
        new Thread(tcpAcceptor).start();

        udpAcceptor = new UDPAcceptor(this);
        new Thread(udpAcceptor).start();

        System.out.println("Server started");
        return this;
    }

    public void shutdown() throws IOException, InterruptedException {
        System.out.println("Stopping server");

        stop = true;
        tcpAcceptor.shutdown();
        udpAcceptor.shutdown();

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Server stopped");
    }

    public long getSum() {
        return sum.get();
    }

    private static class TCPAcceptor implements Runnable {

        private final ServerSocket socket;
        private final Server server;

        public TCPAcceptor(Server server) throws IOException {
            this.server = server;
            socket = new ServerSocket(server.port);
        }

        public void shutdown() throws IOException {
            socket.close();
        }

        @Override
        public void run() {
            while (!server.stop) {
                try {
                    Socket acceptedSocket = socket.accept();
                    server.executor.execute(new TCPHandler(acceptedSocket, server.sum));

                } catch (IOException e) {
                    if (e.getMessage().equals("Socket closed")) {
                        System.out.println("TCP Socket closed");
                    } else {
                        System.out.println("TCPAcceptor error: " + e.getMessage());
                    }
                }
            }
        }
    }

    private static class UDPAcceptor implements Runnable {

        private final DatagramSocket socket;
        private Server server;

        public UDPAcceptor(Server server) throws SocketException {
            this.server = server;
            socket = new DatagramSocket(server.port);
        }

        public void shutdown() throws IOException {
            socket.close();
        }

        @Override
        public void run() {
            while (!server.stop) {
                try {
                    byte[] buf = new byte[10];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    server.executor.execute(new UDPHandler(packet, server.sum, socket));

                } catch (IOException e) {
                    if (e.getMessage().equals("Socket closed")) {
                        System.out.println("UDP Socket closed");
                    } else {
                        System.out.println("TCPAcceptor error: " + e.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int portParam = Utils.DEFAULT_PORT;

        if (args.length == 0) {
            System.out.println("Usage: java bartosh.server.Server <port>");
            System.out.println("Using default port");
            System.out.println();
        } else
            portParam = Utils.parsePort(args[0]);

        final Server server = new Server(portParam).start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    server.shutdown();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
