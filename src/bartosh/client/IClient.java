package bartosh.client;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Fedor Bartosh on 12.06.15
 */
public interface IClient extends Closeable {

    void add(int... numbers) throws IOException;

    void add(long... numbers) throws IOException;

    long get() throws IOException;
}
