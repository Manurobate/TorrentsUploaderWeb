package fr.robate.torrentuploader.exception;

import java.io.Serial;

public class NoConnection extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public NoConnection(final String message) {
        super(message);
    }
}
