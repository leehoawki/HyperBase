package hyperbase.exception;

public class HyperException extends RuntimeException {
    public HyperException() {
        super();
    }

    public HyperException(String message) {
        super(message);
    }

    public HyperException(String message, Throwable cause) {
        super(message, cause);
    }

    public HyperException(Throwable cause) {
        super(cause);
    }
}
