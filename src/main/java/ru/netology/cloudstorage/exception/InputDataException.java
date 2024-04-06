package ru.netology.cloudstorage.exception;

public class InputDataException extends RuntimeException {
    public InputDataException(String message) {
        super(message);
    }

    public InputDataException(String localMessage, String message) {
        super(localMessage + " : " + message);
    }
}
