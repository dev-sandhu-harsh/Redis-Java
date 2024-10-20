import java.io.IOException;

import CachingServer.CacheServer;
import CachingServer.RedisStore;
import RedisServer.RedisServer;

public class Server {
    public static void main(String args[]) throws Exception
    {
        try{
            RedisStore store = new RedisStore();
            Runnable redisServerTask = () -> {
                try{
                    RedisServer server = new RedisServer(store);
                    server.start();
                } catch (IOException e) {
                    System.out.println("Error starting RedisServer: " + e.getMessage());
                }
            };
            Runnable cacheServerTask = () -> {
                CacheServer cacheServer = new CacheServer(store);
                cacheServer.loadCacheFromFile("./CachingServer/cache.dat");
                cacheServer.startCacheSaver();
            };

            Thread redisServerThread = new Thread(redisServerTask);
            redisServerThread.start();

            Thread cacheServerThread = new Thread(cacheServerTask);
            cacheServerThread.start();


            redisServerThread.join();
            cacheServerThread.join();
        }catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
    }
}