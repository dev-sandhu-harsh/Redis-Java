package Exceptions;

public class KeyNotExistInRedisStoreException extends Exception {
    // Default constructor
    public KeyNotExistInRedisStoreException() {
        super("Invalid age provided");
    }

    // Constructor that accepts a custom error message
    public KeyNotExistInRedisStoreException(String message) {
        super(message);
    }

    // Constructor that accepts a custom error message and a cause
    public KeyNotExistInRedisStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
