package bartosh.server;

import bartosh.common.Protocol;
import bartosh.common.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Fedor Bartosh on 11.06.15
 */
class TCPHandler implements Runnable {

    private Socket socket;
    private AtomicLong sum;

    public TCPHandler(Socket socket, AtomicLong sum) throws IOException {
        this.socket = socket;
        this.sum = sum;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    byte commandByte = dataInputStream.readByte();
                    if (commandByte == Protocol.ADD_COMMAND) {
                        processAdd(dataOutputStream, (long) dataInputStream.readInt());

                    } else if (commandByte == Protocol.ADD_LONG_COMMAND) {
                        processAdd(dataOutputStream, dataInputStream.readLong());

                    } else if (commandByte == Protocol.GET_COMMAND) {
                        dataOutputStream.writeLong(sum.get());

                    } else {
                        dataOutputStream.writeByte(Protocol.RESPONSE_UNKNOWN_COMMAND);
                    }
                    dataOutputStream.flush();

                } catch (EOFException e) {
                    break;
                }
            }

            dataInputStream.close();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAdd(DataOutputStream dataOutputStream, long toAdd) throws IOException {
        if (Utils.checkAndAdd(toAdd, sum)) {
            dataOutputStream.writeByte(Protocol.RESPONSE_OK);
        } else {
            dataOutputStream.writeByte(Protocol.RESPONSE_OVERFLOW);
        }
    }
}