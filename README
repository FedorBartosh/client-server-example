
=== Prerequisites

    JDK 1.7+ must be installed
    No third-party libs used


=== Compilation

    I used JDK 1.8 but made it compatible with 1.7 compiler and runtime.

        mkdir bin
        javac -d bin -source 1.7 -target 1.7 src/bartosh/*/* test/*


=== Usage

    1) Go to bin/ directory.

        cd bin

    2) To start server, type

        java bartosh.server.Server 2010

    Server will be started on port 2010. To stop server, press Ctrl + C.

    3) To run client and see current sum, type

        java bartosh.client.TCPClient localhost 2010
        java bartosh.client.UDPClient localhost 2010

    4) To add some numbers, type

        java bartosh.client.TCPClient localhost 2010 12345 67890

    Hex numbers are also supported

        java bartosh.client.TCPClient localhost 2010 0x7fffffff
        java bartosh.client.TCPClient localhost 2010 -0x80000000

    5) Package includes a couple of performance tests (TestConcurrentAdd, TestConcurrentGet) to run it type

        java TestConcurrentAdd

        It starts server (on port 2013) and runs 10 clients (both TCP and UDP),
        each of them sending 100 requests to add [1, 2, 3].


=== Comments

    Clients are not thread-safe. It would make sense to make them thread-safe and add a pool of connections,
    but I didn't want to make the solution over-complicated.

    Sum is stored as a 32-bit int and is protected from overflow - see Utils.checkAndAdd

    Server is able to catch closed socket even if client dies without closing socket on it's side.

    I paid some attention to stopping server correctly, so it stops receiving connections and datagrams first,
    then waits for all submitted tasks to complete and closes sockets.


