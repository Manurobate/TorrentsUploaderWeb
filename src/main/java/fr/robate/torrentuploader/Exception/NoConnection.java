package fr.robate.torrentuploader.Exception;

public class NoConnection extends Exception {
    private static final long serialVersionUID = 1L;


    public NoConnection(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoConnection(final String message) {
        super(message);
    }
}
