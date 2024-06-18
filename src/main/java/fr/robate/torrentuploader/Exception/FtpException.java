package fr.robate.torrentuploader.Exception;

public class FtpException extends Exception {
    public FtpException(String message) {
        super(message);
    }

    public FtpException(String message, Exception e) {
        super(message, e);
    }
}
