package fr.robate.torrentuploader.exception;

import java.io.Serial;

public class UploadFailed extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public UploadFailed(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UploadFailed(final String message) {
        super(message);
    }
}
