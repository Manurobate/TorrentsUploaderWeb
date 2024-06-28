package fr.robate.torrentuploader.exception;

public class IncorrectFile extends Exception {
    private static final long serialVersionUID = 1L;


    public IncorrectFile(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IncorrectFile(final String message) {
        super(message);
    }
}
