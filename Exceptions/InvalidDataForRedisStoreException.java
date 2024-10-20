package Exceptions;

public class InvalidDataForRedisStoreException extends Exception {
    // Default constructor
    public InvalidDataForRedisStoreException() {
        super("Invalid age provided");
    }

    // Constructor that accepts a custom error message
    public InvalidDataForRedisStoreException(String message) {
        super(message);
    }

    // Constructor that accepts a custom error message and a cause
    public InvalidDataForRedisStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
