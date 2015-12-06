package bartosh.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Fedor Bartosh on 12.06.15
 */
public class Utils {

    public static int DEFAULT_PORT = 2015;

    public static int parsePort(String port) {
        int portParam = DEFAULT_PORT;
        try {
            portParam = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            System.out.println(port + " doesn't look like a port number, using default " + DEFAULT_PORT);
        }
        if (portParam < 0 || portParam > 0xFFFF) {
            System.out.println(portParam + " is a wrong a port number, using default " + DEFAULT_PORT);
            portParam = DEFAULT_PORT;
        }
        return portParam;
    }

    public static boolean checkAndAdd(long toAdd, AtomicLong sum) {
        boolean repeat;
        do {
            long current = sum.get();
            long result = current + toAdd;
            if ((toAdd ^ current) < 0 | (toAdd ^ result) >= 0) {
                repeat = !sum.compareAndSet(current, result);
            } else {
                return false;
            }
        } while (repeat);

        return true;
    }
}
