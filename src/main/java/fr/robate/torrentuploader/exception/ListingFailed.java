package fr.robate.torrentuploader.exception;

import java.io.Serial;

public class ListingFailed extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public ListingFailed(final String message, final Throwable cause) {
        super(message, cause);
    }

}
