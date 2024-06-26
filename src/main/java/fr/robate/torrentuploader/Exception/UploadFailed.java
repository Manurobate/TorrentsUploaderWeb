package fr.robate.torrentuploader.Exception;

public class UploadFailed extends Exception {
    private static final long serialVersionUID = 1L;


    public UploadFailed(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UploadFailed(final String message) {
        super(message);
    }
}
