package fr.robate.torrentuploader.Exception;

public class NetworkError extends Exception {
    private static final long serialVersionUID = 1L;


    public NetworkError(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NetworkError(final String message) {
        super(message);
    }
}
