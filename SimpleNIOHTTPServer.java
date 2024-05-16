import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.nio.ByteBuffer;

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
    @Override
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
                        acceptNewConnection(key, selector);

                    } else if (key.isReadable()) {
                        // Handle new connection
                         readRequest(key);
                    }
                }
            }
            
        } catch (IOException e) {
            System.out.println(e);
        }
    
    };

    // defining the connection handler
    private void acceptNewConnection(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

    }

    // defining read reuest handler
    private void readRequest(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = socketChannel.read(buffer);

        if (bytesRead == -1) {
            socketChannel.close();
            return;
        }

        buffer.flip();
        String request = new String(buffer.array(), 0, bytesRead);
        String response = processRequest(request);

        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
        socketChannel.write(responseBuffer);
        socketChannel.close();
    }

    private String processRequest(String request) throws IOException {
        String[] requestLines = request.split("\r\n");
        String requestLine = requestLines[0];
        String[] requestParts = requestLine.split(" ");
        String method = requestParts[0];
        String path = requestParts[1];

        if ("GET".equals(method)) {
            return handleGet(path);
        } else if ("POST".equals(method)) {
            return handlePost(requestLines);
        }

        return "HTTP/1.1 405 Method Not Allowed\r\n\r\n";
    }
    private String handleGet(String path) throws IOException {
        if ("/".equals(path)) {
            path = "/index.html";
        }

        Path filePath = Paths.get("templates" + path);
        if (Files.exists(filePath)) {
            String content = new String(Files.readAllBytes(filePath));
            return "HTTP/1.1 200 OK\r\nContent-Length: " + content.length() + "\r\n\r\n" + content;
        }

        return "HTTP/1.1 404 Not Found\r\n\r\n";
    }

    private String handlePost(String[] requestLines) throws IOException {
        StringBuilder body = new StringBuilder();
        boolean isBody = false;

        for (String line : requestLines) {
            if (isBody) {
                body.append(line);
            }
            if (line.isEmpty()) {
                isBody = true;
            }
        }

        String[] bodyParams = body.toString().split("&");
        String username = null;
        String email = null;

        for (String param : bodyParams) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                if ("username".equals(keyValue[0])) {
                    username = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                } else if ("email".equals(keyValue[0])) {
                    email = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                }
            }
        }

        if (username != null && email != null) {
            Files.write(Paths.get("db.txt"), (username + " " + email + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            return "HTTP/1.1 200 OK\r\n\r\nSuccess";
        }

        return "HTTP/1.1 400 Bad Request\r\n\r\n";
    }



    
}