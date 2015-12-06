package bartosh.server;

import bartosh.common.Protocol;
import bartosh.common.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Fedor Bartosh on 11.06.15
 */
class UDPHandler implements Runnable {

    private DatagramPacket packet;
    private AtomicLong sum;
    private DatagramSocket socket;

    public UDPHandler(DatagramPacket packet, AtomicLong sum, DatagramSocket socket) {
        this.packet = packet;
        this.sum = sum;
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] data = packet.getData();
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte commandByte = buf.get();

        ByteBuffer responseBuffer;
        if (commandByte == Protocol.ADD_COMMAND) {
            responseBuffer = processAdd((long)buf.getInt());

        } else if (commandByte == Protocol.ADD_LONG_COMMAND) {
            responseBuffer = processAdd(buf.getLong());

        } else if (commandByte == Protocol.GET_COMMAND) {
            responseBuffer = ByteBuffer.allocate(8);
            responseBuffer.putLong(sum.get());

        } else {
            responseBuffer = ByteBuffer.allocate(1);
            responseBuffer.put(Protocol.RESPONSE_UNKNOWN_COMMAND);
        }

        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        packet = new DatagramPacket(responseBuffer.array(), responseBuffer.array().length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer processAdd(long toAdd) {
        ByteBuffer responseBuffer = ByteBuffer.allocate(1);
        if (Utils.checkAndAdd(toAdd, sum)) {
            responseBuffer.put(Protocol.RESPONSE_OK);
        } else {
            responseBuffer.put(Protocol.RESPONSE_OVERFLOW);
        }
        return responseBuffer;
    }
}
