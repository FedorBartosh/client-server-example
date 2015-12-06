package bartosh.client;

import java.io.IOException;

/**
 * Created by Fedor Bartosh on 12.06.15
 */
abstract class AbstractClient implements IClient {

    public abstract void add(int... numbers) throws IOException;

    public abstract void add(long... numbers) throws IOException;

    public abstract long get() throws IOException;

    protected static int[] parseNumbers(String[] args) {
        int[] ints = new int[args.length - 2];
        for (int i = 0; i < args.length - 2; i++) {
            try {
                ints[i] = Integer.decode(args[i + 2]);
            } catch (NumberFormatException e) {
                System.out.println(args[i + 2] + " is not an int32, exit");
                System.exit(1);
            }
        }
        return ints;
    }
}
