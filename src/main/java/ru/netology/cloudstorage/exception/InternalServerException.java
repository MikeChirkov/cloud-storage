package ru.netology.cloudstorage.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String localMessage, String message) {
        super(localMessage + " : " + message);
    }
}
