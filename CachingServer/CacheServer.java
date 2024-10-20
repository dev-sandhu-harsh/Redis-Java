package CachingServer;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;

class RedisStoreEntry implements Serializable{
    String value;
    long expirationTime;
    public RedisStoreEntry(String value){
        this.value = value;
        this.expirationTime = -1;
    }
    public RedisStoreEntry(String value, long expirationTime){
        this.value = value;
        this.expirationTime = expirationTime;
    }
    public void addExpireTime(long expirationTime){
        this.expirationTime = expirationTime;
    }
    @Override
    public String toString() {
        return "RedisStoreEntry [value=" + value + ", expirationTime=" + expirationTime + "]";
    }
}

public class CacheServer implements Serializable{
    private Map<String,RedisStoreEntry> dataStore;
    RedisStore store;
    public CacheServer(RedisStore store){
        this.store = store;
    }
    public void saveCacheToFile(String fileName){
        System.out.println("Caching...");
        this.dataStore = store.getRedisDataStore();
        try(FileOutputStream fileOut = new FileOutputStream(fileName)){
            try(ObjectOutputStream out = new ObjectOutputStream(fileOut)){
                out.writeObject(dataStore);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadCacheFromFile(String fileName) {
        try (FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn)) {
            dataStore = (ConcurrentHashMap<String, RedisStoreEntry>) in.readObject();
            // Clean expired entries after loading
            dataStore.entrySet().removeIf(entry -> 
                {
                    long currentTime = System.currentTimeMillis();
                    long expirationTime = entry.getValue().expirationTime;
                    if(expirationTime != -1 && expirationTime < currentTime){
                        return true;
                    }else{
                        return false;
                    }
                }
            );
            store.setRedisDataStore(dataStore);
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void startCacheSaver() {
        System.out.println("Starting Caching Server...");
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> saveCacheToFile("./CachingServer/cache.dat"), 0, 10, TimeUnit.SECONDS);
    }
}
