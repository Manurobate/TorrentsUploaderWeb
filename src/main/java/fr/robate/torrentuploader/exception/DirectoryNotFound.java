package fr.robate.torrentuploader.exception;

import java.io.Serial;

public class DirectoryNotFound extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;


    public DirectoryNotFound(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DirectoryNotFound(final String message) {
        super(message);
    }
}
