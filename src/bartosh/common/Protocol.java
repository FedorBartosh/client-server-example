package bartosh.common;

/**
 * Created by Fedor Bartosh on 12.06.15
 */
public class Protocol {

    // commands
    public static final byte GET_COMMAND = 0x0;

    public static final byte ADD_COMMAND = 0x1;
    public static final byte ADD_LONG_COMMAND = 0x2;

    // response codes
    public static final byte RESPONSE_OK = 0x0;
    public static final byte RESPONSE_OVERFLOW = 0x1;
    public static final byte RESPONSE_UNKNOWN_COMMAND = 0x2;
}
