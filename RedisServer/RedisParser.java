package RedisServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

import Exceptions.InvalidDataForRedisStoreException;
import Exceptions.KeyNotExistInRedisStoreException;
import CachingServer.RedisStore;
import RedisServer.Commands.Commands;
import RedisServer.Commands.Options.SetCommandOptions;

class RedisString {
    int length;
    String data;
    RedisString(int length, String data){
        this.length = length;
        this.data = data;
    }
}
class RedisCommand {
    private BufferedReader input;
    private OutputStream out;
    private RedisStore store;
    RedisCommand(BufferedReader input, OutputStream out, RedisStore store){
        this.input=input;
        this.out=out;
        this.store = store;
    }
    private Boolean matchStringToCommand(String str, Commands cmd){
        return cmd.name().toLowerCase().equals(str.toLowerCase());
    }
    public Commands getRedisCommand(String command){
        if(matchStringToCommand(command,Commands.PING)){
            return Commands.PING;
        }else if(matchStringToCommand(command,Commands.ECHO)){
            return Commands.ECHO;
        }else if(matchStringToCommand(command,Commands.GET)){
            return Commands.GET;
        }else if(matchStringToCommand(command,Commands.SET)){
            return Commands.SET;
        }else if(matchStringToCommand(command,Commands.COMMAND)){
            return Commands.COMMAND;
        }
        return null;
    }
    public void execPing() throws IOException{
        out.write("+PONG\r\n".getBytes());
    }
    public void execEcho() throws IOException{
        input.readLine(); // length of input string
        String message = input.readLine();
        String formattedMsg = String.format("$%d\r\n%s\r\n", message.length(), message);
        out.write(formattedMsg.getBytes());
    }
    public void execSet(int dataLength) throws IOException, InvalidDataForRedisStoreException{
        try{
            input.readLine(); // length of key 
            String key = input.readLine();
            input.readLine(); //length of value
            String value = input.readLine();
            SetCommandOptions options = new SetCommandOptions();
            String len = null;
            int x = 3;
            while(x<dataLength){
                len = input.readLine(); // length
                if(len == null || len.length() == 0){
                    break;
                }
                String option = input.readLine();
                x++;
                if(option == null || option.length() == 0){
                    break;
                }
                if(option.toLowerCase().equals("nx")){
                    options.setNXXXOptions("nx",true);
                }
                if(option.toLowerCase().equals("xx")){
                    options.setNXXXOptions("xx",true);
                }
                if(option.toLowerCase().equals("ex")){
                    input.readLine(); // length
                    var optionValue = Integer.parseInt(input.readLine());
                    x++;
                    options.setExpireTime("ex",optionValue);
                }
                if(option.toLowerCase().equals("px")){
                    input.readLine(); // length
                    var optionValue = Integer.parseInt(input.readLine());
                    x++;
                    options.setExpireTime("px",optionValue);
                }
                if(option.toLowerCase().equals("exat")){
                    input.readLine(); // length
                    var optionValue = Long.parseLong(input.readLine());
                    x++;
                    options.setUnixExpireTime("exat",optionValue);
                }
                if(option.toLowerCase().equals("pxat")){
                    input.readLine(); // length
                    var optionValue = Long.parseLong(input.readLine());
                    x++;
                    options.setUnixExpireTime("pxat",optionValue);
                }
                if(option.toLowerCase().equals("get")){
                    options.setGETOption(true);
                }
                if(option.toLowerCase().equals("keepttl")){
                    options.setKEEPTTLOption(true);
                }
            }
            if(options.GET){
                try{
                    String curr_value = store.getDataFromStore(key);
                    out.write(String.format("$%d\r\n%s\r\n", curr_value.length(), curr_value).getBytes());
                }catch(KeyNotExistInRedisStoreException ex){
                    // ignore
                }
            }
            store.addDataToStore(key, value, options);
            out.write("+OK\r\n".getBytes());
        }catch(Exception ex){
            out.write(String.format("+%s\r\n", "Error: Invalid Options -> " + ex.getMessage()).getBytes());
        }
    }
    public void execGet() throws IOException, InvalidDataForRedisStoreException{
        try{
            input.readLine(); // length of key 
            String key = input.readLine();
            String value = store.getDataFromStore(key);
            if(value == null){
                out.write("-1\r\n".getBytes());
            }else{
                out.write(String.format("$%d\r\n%s\r\n", value.length(), value).getBytes());
            }
        }catch (KeyNotExistInRedisStoreException ex){
            out.write("-1\r\n".getBytes());
        }
    }
    public void execCommand() throws IOException{
        input.readLine();
        input.readLine();
        out.write("+OK\r\n".getBytes());
    }
}

public class RedisParser {
    private BufferedReader input;
    private OutputStream out;
    private RedisStore store;
    public RedisParser(BufferedReader input, OutputStream out, RedisStore store){
        this.input = input;
        this.out = out;
        this.store = store;
    }

    private RedisString parseBulkString() throws IOException{
        try{
            input.readLine(); // input for string length
            String strValue = input.readLine();
            if(strValue == null || strValue.length() == 0){
                return null;
            }
            RedisString redisString = new RedisString(strValue.length(), strValue);
            return redisString;
        }
        catch(IOException ioex){

        }catch(Exception ex){

        }
        return null;
    }

    private List<RedisString> parseArray(int arrlength, BufferedReader input) throws IOException{
        try{
            List<RedisString> array = new ArrayList<RedisString>();
            while(arrlength-- > 0){
                var redisString = parseBulkString();
                if(redisString != null){
                    array.add(redisString);
                }
            }
            return array;
        }
        catch(IOException ioex){

        }catch(Exception ex){

        }
        return null;
    }

    private void printRedisArray(List<RedisString> array) throws IOException{
        String output = String.format("*%d\r\n", array.size());
        for(RedisString redisString: array){
            if(redisString == null){
                return;
            }
            output += String.format("$%d\r\n%s\r\n", redisString.length, redisString.data);
        }
        out.write(output.getBytes());
    }

    private void executeCommand(String command, int dataLength) throws IOException, InvalidDataForRedisStoreException{
        RedisCommand redisCommandExecuter = new RedisCommand(input, out, store);
        var cmd = redisCommandExecuter.getRedisCommand(command);
        if(cmd == null){
            return;
        }
        switch(cmd){
            case Commands.PING:
                redisCommandExecuter.execPing();
                break;
            case Commands.ECHO:
                redisCommandExecuter.execEcho();
                break;
            case Commands.GET:
                redisCommandExecuter.execGet();
                break;
            case Commands.SET:
                redisCommandExecuter.execSet(dataLength);
                break;
            case Commands.COMMAND:
                redisCommandExecuter.execCommand();
                break;
            default:
                break;
        }
    }

    public void parseRedisCommand() throws IOException, InvalidDataForRedisStoreException{
        String dataTypeStr = null;
        String command = null;
        while(true){
            dataTypeStr = input.readLine();
            if(dataTypeStr == null || dataTypeStr == ""){
                continue;
            }
            String dataType = dataTypeStr.substring(0,1);
            int dataLength = Integer.parseInt(dataTypeStr.substring(1));

            input.readLine(); //command string length
            command = input.readLine();
            if(command == null || command == ""){
                continue;
            }
            switch (dataType) {
                case "+": //Simple String
                    
                    break;
                case "-": //Simple Error
                    
                    break;
                case ":": //Integers
                    
                    break;
                case "$": //Bulk Strings
                    
                    break;
                case "*": //Arrays
                     executeCommand(command, dataLength);
                     break;
                case "_": //Nulls
                    
                    break;
                case "#": //Booleans
                    
                    break;
                case ",": //Doubles
                    
                    break;
                case "(": //Big numbers
                    
                    break;
                case "!": //Bulk erroes
                    
                    break;
                case "=": //Verbatim strings
                    
                    break;
                case "%": //Maps
                    
                    break;
                case "`": //Attributes
                    
                    break;
                case "~": //Sets
                    
                    break;
                case ">": //Pushes
                    
                    break;
                default:
                    break;
            }
        }
    }
}
