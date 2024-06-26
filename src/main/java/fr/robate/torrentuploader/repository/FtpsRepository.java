package fr.robate.torrentuploader.repository;

import fr.robate.torrentuploader.Exception.FtpException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;

@Repository
public class FtpsRepository {

    private FTPSClient ftpsClient;
    private boolean isConnected = false;
    private boolean isLogged = false;

    public void connect(String host, int port, String user, String password) throws FtpException {
        ftpsClient = new FTPSClient(false);

        try {
            ftpsClient.connect(host, port);
            isConnected = FTPReply.isPositiveCompletion(ftpsClient.getReplyCode());

            if (!isConnected)
                throw new FtpException("Error while connecting to host " + host + ":" + port);

            isLogged = ftpsClient.login(user, password);

            if (!isLogged)
                throw new FtpException("Authentication error with user " + user);

            ftpsClient.execPROT("P");
            ftpsClient.setFileType(FTPSClient.BINARY_FILE_TYPE);
            ftpsClient.setFileTransferMode(FTPSClient.STREAM_TRANSFER_MODE);
            ftpsClient.enterLocalPassiveMode();
        } catch (IOException e) {
            throw new FtpException("Network error during connection", e);
        }
    }

    public void disconnect() throws FtpException {
        try {
            ftpsClient.logout();
            isLogged = false;

            ftpsClient.disconnect();
            isConnected = false;

        } catch (IOException e) {
            throw new FtpException("Error during disconnecting", e);
        }
    }

    private void checkIfConnectedAndLogged() throws FtpException {
        if (!isConnected)
            throw new FtpException("Not connected. Please use connect() method first");
        else if (!isLogged)
            throw new FtpException("Not logged. Please use connect() method first");
    }

    public FTPFile[] listDirectories(String path) throws FtpException {
        checkIfConnectedAndLogged();

        try {
            ftpsClient.changeWorkingDirectory(path);

            return ftpsClient.listDirectories();
        } catch (IOException e) {
            throw new FtpException("Error during directory listing", e);
        }
    }

    public void uploadFile(InputStream file, String storingPath, String fileName) throws FtpException {
        checkIfConnectedAndLogged();

        try {
            ftpsClient.changeWorkingDirectory(storingPath);
        } catch (IOException e) {
            throw new FtpException("Error : Can't change directory", e);
        }

        if (file == null)
            throw new FtpException("Error InputStream is null");

        try {
            if (ftpsClient.storeFile(fileName, file))
                file.close();
            else
                throw new FtpException("Error during file storing");
        } catch (IOException e) {
            throw new FtpException("Error during file uploading", e);
        }
    }

}
