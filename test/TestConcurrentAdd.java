import bartosh.client.IClient;
import bartosh.client.TCPClient;
import bartosh.client.UDPClient;
import bartosh.server.Server;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fedorbartosh on 12.06.15
 */
public class TestConcurrentAdd {

    private static final int PORT = 2013;
    private static final int NUMBER_OF_CLIENTS = 5;
    private static final int ITERATIONS = 1000;
    private static final int[] DATA_SET = new int[] {1, 2, 3};

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(PORT).start();

        CountDownLatch startLatch = new CountDownLatch(2 * NUMBER_OF_CLIENTS);
        CountDownLatch stopLatch = new CountDownLatch(2 * NUMBER_OF_CLIENTS);

        for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
            new Thread(new ClientThread("tcp-client-" + i, new TCPClient("localhost", PORT), startLatch, stopLatch)).start();
            new Thread(new ClientThread("udp-client-" + i, new UDPClient("localhost", PORT), startLatch, stopLatch)).start();
        }

        stopLatch.await();

        // wait for all packets to arrive
        Thread.sleep(1000);
        server.shutdown();

        int sum = 0;
        for (int element : DATA_SET) {
            sum += element;
        }

        int expected = NUMBER_OF_CLIENTS * 2 * ITERATIONS * sum;
        System.out.println("EXPECTED SUM: " + expected);
        System.out.println("REAL SUM: " + server.getSum());
    }

    private static class ClientThread implements Runnable {

        private String name;
        private IClient client;
        private final CountDownLatch startLatch;
        private CountDownLatch stopLatch;

        private ClientThread(String name, IClient client, CountDownLatch startLatch, CountDownLatch stopLatch) {
            this.name = name;
            this.client = client;
            this.startLatch = startLatch;
            this.stopLatch = stopLatch;
        }

        @Override
        public void run() {
            try {
                startLatch.countDown();
                startLatch.await();
                long start = System.nanoTime();

                for (int i = 0; i < ITERATIONS; i++) {
                    client.add(DATA_SET);
                }

                long end = System.nanoTime();
                client.close();

                long execTimeNs = end - start;
                float nsPerRequest = ((float) execTimeNs) / ITERATIONS * DATA_SET.length;
                System.out.println(name + " sending done in " + execTimeNs + " ns; " + nsPerRequest + " ns/request");

                stopLatch.countDown();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
