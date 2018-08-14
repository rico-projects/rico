package dev.rico.internal.remoting.communication.codec;

public class CodecException extends RuntimeException {


    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

}
