package RedisServer.Commands.Options;

class InvalidOptionsException extends Exception{
    // Default constructor
    public InvalidOptionsException() {
        super("Invalid age provided");
    }

    // Constructor that accepts a custom error message
    public InvalidOptionsException(String message) {
        super(message);
    }

    // Constructor that accepts a custom error message and a cause
    public InvalidOptionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
public class SetCommandOptions {
    public Boolean NX;
    public Boolean XX;
    public Boolean GET;
    public int EX;
    public int PX;
    public long EXAT;
    public long PXAT;
    public Boolean KEEPTTL;
    public SetCommandOptions(){
        this.NX = false;
        this.XX = false;
        this.GET = false;
        this.EX = -1;
        this.PX = -1;
        this.EXAT = -1;
        this.PXAT = -1;
        this.KEEPTTL = false;
    }
    public void setNXXXOptions(String nx_xx, Boolean nx_xx_value) throws InvalidOptionsException{
        if(nx_xx.toLowerCase().equals("nx")){
            this.NX = nx_xx_value;
        }else if(nx_xx.toLowerCase().equals("xx")){
            this.XX = nx_xx_value;
        }else{
            throw new InvalidOptionsException("Invalid NX|XX option");
        }
        if(this.NX == true && this.XX == true){
            throw new InvalidOptionsException("Can't pass both NX and XX at same time");
        }
    }
    public void setGETOption(Boolean get){
        this.GET = get;
    }
    public void setExpireTime(String ex_px, int ex_px_value) throws InvalidOptionsException{
        if(ex_px_value < 0){
            throw new InvalidOptionsException("Invalid Value for EX|PX option");
        }
        if(ex_px.toLowerCase().equals("ex")){
            this.EX = ex_px_value;
        }else if(ex_px.toLowerCase().equals("px")){
            this.PX = ex_px_value;
        }else{
            throw new InvalidOptionsException("Invalid EX|PX option");
        }
        if(this.EX != -1 && this.PX != -1){
            throw new InvalidOptionsException("Can't set both EX and PX at same time");
        }
        if(this.EXAT != -1 || this.PXAT != -1){
            throw new InvalidOptionsException("Can't set of " + ex_px.toUpperCase() + " while EXAT|PXAT is already set");
        }
    }
    public void setUnixExpireTime(String exat_pxat, long exat_pxat_value) throws InvalidOptionsException{
        if(exat_pxat_value < 0){
            throw new InvalidOptionsException("Invalid Value for EXAT|PXAT option");
        }
        if(exat_pxat.toLowerCase().equals("exat")){
            this.EXAT = exat_pxat_value;
        }else if(exat_pxat.toLowerCase().equals("pxat")){
            this.PXAT = exat_pxat_value;
        }else{
            throw new InvalidOptionsException("Invalid EXAT|PXAT option");
        }
        if(this.EXAT != -1 && this.PXAT != -1){
            throw new InvalidOptionsException("Can't set both EXAT and PXAT at same time");
        }
        if(this.EX != -1 || this.PX != -1){
            throw new InvalidOptionsException("Can't set of " + exat_pxat.toUpperCase() + " while EX|PX is already set");
        }
    }
    public void setKEEPTTLOption(Boolean keepttl) throws InvalidOptionsException{
        this.KEEPTTL = keepttl;
    }
}
