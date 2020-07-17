package exception;

public class UserAuthenticationException extends RuntimeException{
    public UserAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    public UserAuthenticationException(String message) {
        super(message);
    }
}
