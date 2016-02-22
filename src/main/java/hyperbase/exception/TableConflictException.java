package hyperbase.exception;

public class TableConflictException extends HyperException {
    public TableConflictException() {
        super();
    }

    public TableConflictException(String message) {
        super(message);
    }

    public TableConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableConflictException(Throwable cause) {
        super(cause);
    }
}

