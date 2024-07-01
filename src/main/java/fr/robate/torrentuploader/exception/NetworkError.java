package fr.robate.torrentuploader.exception;

import java.io.Serial;

public class NetworkError extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public NetworkError(final String message, final Throwable cause) {
        super(message, cause);
    }

}
