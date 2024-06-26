package fr.robate.torrentuploader.Exception;

public class LoginDenied extends Exception {
    private static final long serialVersionUID = 1L;


    public LoginDenied(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LoginDenied(final String message) {
        super(message);
    }
}
