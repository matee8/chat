package hu.progtech.chat.util;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TokenValidationException(String msg) {
        super(msg);
    }
}
