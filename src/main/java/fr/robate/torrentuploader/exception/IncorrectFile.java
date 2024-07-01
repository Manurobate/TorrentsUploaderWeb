package fr.robate.torrentuploader.exception;

import java.io.Serial;

public class IncorrectFile extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public IncorrectFile(final String message) {
        super(message);
    }
}
