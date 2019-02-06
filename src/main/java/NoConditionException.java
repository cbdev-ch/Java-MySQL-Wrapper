public class NoConditionException extends RuntimeException{
    public NoConditionException() {
        super("There was no condition in the database request");
    }

    public NoConditionException(String detail) {
        super("There was no '" + detail + "' condition in the database request");
    }

    public NoConditionException(Throwable cause) {
        super("There was no condition in the database request", cause);
    }

    public NoConditionException(String detail, Throwable cause) {
        super("There was no '" + detail + "' condition in the database request", cause);
    }
}
