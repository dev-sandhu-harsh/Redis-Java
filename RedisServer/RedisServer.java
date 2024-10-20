package RedisServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

import CachingServer.RedisStore;

import java.util.concurrent.ExecutorService;

public class RedisServer{
    private final int DEFAULT_PORT = 6379; //by Default redis runs on port 6379
    private final int THREAD_POOL_LIMIT = 10;
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private RedisStore store;

    public RedisServer(RedisStore store) throws IOException {
        this.port = DEFAULT_PORT;
        this.serverSocket = new ServerSocket(DEFAULT_PORT);
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_LIMIT);
        this.store = store;
    }
    public RedisServer(int port, RedisStore store) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(this.port);
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_LIMIT);
        this.store = store;
    }
    public void start(){
        System.out.println("Redis server started on port " + serverSocket.getLocalPort());
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client Connected: " + clientSocket.getInetAddress().getHostAddress());

                // each client on separate thread
                executor.submit(new ClientHandler(clientSocket, store));
            }catch(IOException ex){
                System.out.println("IOException: " + ex.getMessage());
            }
        }
    }
}