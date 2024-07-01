package fr.robate.torrentuploader.exception;

import java.io.Serial;

public class LoginDenied extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public LoginDenied(final String message) {
        super(message);
    }
}
