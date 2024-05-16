public class HTTPServerRunner {

    public static void main(String[] args) {

        if (args.length != 2){
            System.out.println("bad arguments!");
            System.exit(1);
        }

        String bindAddress = args[0];   // Initialize with first commandline program argument
        int bindPort=-1;                 
        // Initialize with second commandline program argument
        try{
            bindPort = Integer.parseInt(args[1]); 
        } catch(NumberFormatException e) {
            System.err.println("Invalid port number :"+ args[1]);
            System.exit(1);
        }

        SimpleNIOHTTPServer simpleNioHttpServer = new SimpleNIOHTTPServer(bindAddress, bindPort);
        simpleNioHttpServer.run();
    }
}
