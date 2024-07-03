package fr.robate.torrentuploader.repository;

import fr.robate.torrentuploader.exception.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Data
@Repository
public class FtpsRepository {

    private FTPSClient ftpsClient;
    private boolean isConnected = false;
    private boolean isLogged = false;

    public void connect(String host, int port, String user, String password) throws NoConnection, LoginDenied, NetworkError {
        ftpsClient = new FTPSClient(false);

        log.debug("Connecting to {}:{}", host, port);

        try {
            ftpsClient.connect(host, port);
            isConnected = FTPReply.isPositiveCompletion(ftpsClient.getReplyCode());

            if (!isConnected)
                throw new NoConnection("Error while connecting to host " + host + ":" + port);
            else
                log.debug("Connected to {}:{}", host, port);

            isLogged = ftpsClient.login(user, password);

            if (!isLogged)
                throw new LoginDenied("Authentication error with user " + user);
            else
                log.debug("Logged into {}:{} with user {}", host, port, user);

            ftpsClient.execPROT("P");
            ftpsClient.setFileType(2); // BINARY_FILE_TYPE
            ftpsClient.setFileTransferMode(10); // STREAM_TRANSFER_MODE
            ftpsClient.enterLocalPassiveMode();

            log.debug("Settings ok");
        } catch (IOException e) {
            throw new NetworkError("Network error during connection", e);
        }
    }

    public void disconnect() throws NetworkError {
        try {
            if (ftpsClient.isConnected()) {
                log.debug("Disconnecting");

                ftpsClient.logout();
                isLogged = false;
                log.debug("Logged out");

                ftpsClient.disconnect();
                isConnected = false;
                log.debug("Disconnected");
            }
        } catch (IOException e) {
            throw new NetworkError("Error during disconnecting", e);
        }
    }

    private void checkIfConnectedAndLogged() throws NoConnection, LoginDenied {
        if (!isConnected)
            throw new NoConnection("Not connected. Please use connect() method first");
        else if (!isLogged)
            throw new LoginDenied("Not logged. Please use connect() method first");
    }

    public FTPFile[] listDirectories(String path) throws NoConnection, LoginDenied, ListingFailed {
        checkIfConnectedAndLogged();

        log.debug("Listing directories");

        try {
            ftpsClient.changeWorkingDirectory(path);

            return ftpsClient.listDirectories();
        } catch (IOException e) {
            throw new ListingFailed("Error during directory listing", e);
        }
    }

    public void uploadFile(InputStream file, String storingPath, String fileName) throws NoConnection, LoginDenied, UploadFailed, IncorrectFile, DirectoryNotFound {
        checkIfConnectedAndLogged();

        log.debug("Uploading file {}", fileName);

        try {
            ftpsClient.changeWorkingDirectory(storingPath);
        } catch (IOException e) {
            throw new DirectoryNotFound("Error : Can't change directory", e);
        }

        if (file == null)
            throw new IncorrectFile("Error InputStream is null");

        try {
            if (ftpsClient.storeFile(fileName, file)) {
                file.close();
                log.debug("Upload ok");
            } else
                throw new UploadFailed("Error during file storing");
        } catch (IOException e) {
            throw new UploadFailed("Error during file uploading", e);
        }
    }

}
