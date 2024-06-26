package fr.robate.torrentuploader.Exception;

public class DirectoryNotFound extends Exception {
    private static final long serialVersionUID = 1L;


    public DirectoryNotFound(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DirectoryNotFound(final String message) {
        super(message);
    }
}
