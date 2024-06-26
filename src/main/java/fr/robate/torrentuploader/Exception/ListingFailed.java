package fr.robate.torrentuploader.Exception;

public class ListingFailed extends Exception {
    private static final long serialVersionUID = 1L;


    public ListingFailed(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ListingFailed(final String message) {
        super(message);
    }
}
