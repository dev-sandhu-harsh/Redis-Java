package CachingServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import RedisServer.Commands.Options.SetCommandOptions;
import Exceptions.InvalidDataForRedisStoreException;
import Exceptions.KeyNotExistInRedisStoreException;

public class RedisStore {
    private Map<String,RedisStoreEntry> dataStore;
    public RedisStore(){
        dataStore = new ConcurrentHashMap<String, RedisStoreEntry>();
    }
    public Map<String,RedisStoreEntry> getRedisDataStore(){
        return dataStore;
    }
    public void setRedisDataStore(Map<String,RedisStoreEntry> data){
        this.dataStore = data;
    }
    public void addDataToStore(String key, String value, SetCommandOptions options) throws InvalidDataForRedisStoreException{
        if(key == null || key.length() == 0 || value == null || value.length() == 0){
            throw new InvalidDataForRedisStoreException();
        }
        RedisStoreEntry currentRedisValue = null;
        if(dataStore.containsKey(key)){
            currentRedisValue = dataStore.get(key);
        }
        RedisStoreEntry redisValue = new RedisStoreEntry(value);
        if(options.NX){
            if(!dataStore.containsKey(key)){
                dataStore.put(key, redisValue);
            }
        }else if(options.XX){
            if(dataStore.containsKey(key)){
                dataStore.replace(key, redisValue);
            }
        }else{
            dataStore.put(key, redisValue);
        }
        if(options.KEEPTTL && currentRedisValue != null){
            var tempRedisValue = dataStore.get(key);
            tempRedisValue.addExpireTime(currentRedisValue.expirationTime);
            dataStore.replace(key, tempRedisValue);
        }else{
            if(options.EX > 0){
                long expirationTime = (options.EX*1000)+System.currentTimeMillis();
                var tempRedisValue = dataStore.get(key);
                tempRedisValue.addExpireTime(expirationTime);
                dataStore.replace(key, tempRedisValue);
            }else if(options.PX > 0){
                long expirationTime = options.PX+System.currentTimeMillis();
                var tempRedisValue = dataStore.get(key);
                tempRedisValue.addExpireTime(expirationTime);
                dataStore.replace(key, tempRedisValue);
            }else if(options.EXAT > 0){
                long currentTime = System.currentTimeMillis()/1000;
                if(options.EXAT > currentTime){
                    var tempRedisValue = dataStore.get(key);
                    tempRedisValue.addExpireTime(options.EXAT*1000);
                    dataStore.replace(key, tempRedisValue);
                }else{
                    dataStore.remove(key);
                }
            }else if(options.PXAT > 0){
                long currentTime = System.currentTimeMillis();
                if(options.PXAT > currentTime){
                    var tempRedisValue = dataStore.get(key);
                    tempRedisValue.addExpireTime(options.PXAT);
                    dataStore.replace(key, tempRedisValue);
                }else{
                    dataStore.remove(key);
                }
            }
        }
    }
    public String getDataFromStore(String key) throws InvalidDataForRedisStoreException, KeyNotExistInRedisStoreException{
        if(key == null || key.length() == 0){
            throw new InvalidDataForRedisStoreException();
        }
        if(!dataStore.containsKey(key)){
            throw new KeyNotExistInRedisStoreException(key+" doesnot exist in store");
        }
        long currentTime = System.currentTimeMillis();
        RedisStoreEntry redisStoreEntry = dataStore.get(key);
        if(redisStoreEntry.expirationTime == -1 || redisStoreEntry.expirationTime >= currentTime){
            return redisStoreEntry.value;
        }else{
            dataStore.remove(key);
            return null;
        }
    }
}
