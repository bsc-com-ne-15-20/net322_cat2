import java.nio.channels.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

class SimpleNIOHTTPServer implements HTTPServerHandler {
    
    /*
        Implement a simple NIO HTTP server that handle concurrent HHTP clients
        and serves requested template resources.
        In addition, it should respond to POST requests and respond with a success message
        */

    private String bindAddress;
    private int bindPort;

    //TODO 
    public SimpleNIOHTTPServer(String bindAddress, int bindPort){
        // Implement constructor
        this.bindAddress = bindAddress;
        this.bindPort = bindPort;
    }

    // TODO
    /*
     * Handles the core server functionality
     * 
     * Wrapper instance method for running server
     */
    public void run(){
        // Implement run method
        Selector selector ;
        ServerSocketChannel serverSocketChannel;
        try{
            //opening channel and selector
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            // binding address and port
            serverSocketChannel.bind( new java.net.InetSocketAddress(bindAddress,bindPort));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("HTTP server listening on " + serverSocketChannel);

            //handle requests through the loop

            while(true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    if (key.isAcceptable()) {
                        // Handle new connection
                        //acceptNewConnection(key, selector);

                    } else if (key.isReadable()) {
                        // Handle new connection
                        //handleRequest(key);
                    }
                }
            }
            
        } catch (IOException e) {
            System.out.println(e);
        }
    
    };
    
}