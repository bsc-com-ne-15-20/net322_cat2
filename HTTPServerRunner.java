public class HTTPServerRunner {

    public static void main(String[] args) {

        String bindAddress; // Initialize with first commandline program argument
        int bindPort; // Initialize with second commandline program argument

        SimpleNIOHTTPServer simpleNioHttpServer = new SimpleNIOHTTPServer(bindAddress, bindPort);
        simpleNioHttpServer.run();
    }
}
