package ch.cbdev.mysqlwrapper;

public class UnspecifiedColumnException extends RuntimeException {
    public UnspecifiedColumnException() {
        super("Columns where not specified correctly");
    }

    public UnspecifiedColumnException(Throwable cause){
        super("Columns where not specified correctly", cause);
    }

    public UnspecifiedColumnException(String detail) {
        super("The column '" + detail + "' was not specified correctly");
    }

    public UnspecifiedColumnException(String detail, Throwable cause) {
        super("The column '" + detail + "' was not specified correctly", cause);
    }
}
