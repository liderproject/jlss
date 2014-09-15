package jlss;

/**
 * Indicates that something went wrong when processing a Json document
 */
public class JsonException extends RuntimeException {
    public JsonException() {
        super();
    }
    public JsonException(String message) {
        super(message);
    }
    public JsonException(Throwable cause) {
        super(cause);
    }
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
