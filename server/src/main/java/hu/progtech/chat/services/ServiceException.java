package hu.progtech.chat.services;

public class ServiceException extends RuntimeException {
    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ServiceException(String msg) {
        super(msg);
    }
}
