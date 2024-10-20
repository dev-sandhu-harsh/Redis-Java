package RedisServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import Exceptions.InvalidDataForRedisStoreException;
import CachingServer.RedisStore;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private RedisStore store;
    private BufferedReader input;
    private OutputStream out;

    public ClientHandler(Socket socket, RedisStore store){
        this.clientSocket = socket;
        this.store = store;
    }

    @Override
    public void run(){
        try{
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = clientSocket.getOutputStream();
            RedisParser redisParser = new RedisParser(input, out, store);
            redisParser.parseRedisCommand();
        }catch(IOException ex){
            System.out.println("IOException: " + ex.getMessage());
        }catch(InvalidDataForRedisStoreException ex){
            System.out.println("InvalidDataForRedisStoreException: " + ex.getMessage());
        }finally{
            try {
                input.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}