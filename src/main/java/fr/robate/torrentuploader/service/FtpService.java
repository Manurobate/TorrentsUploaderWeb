package fr.robate.torrentuploader.service;

import fr.robate.torrentuploader.configuration.FtpProperties;
import fr.robate.torrentuploader.exception.*;
import fr.robate.torrentuploader.model.FileToUpload;
import fr.robate.torrentuploader.repository.FtpsRepository;
import lombok.Data;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class FtpService {

    private final FtpProperties props;

    private FtpsRepository ftpsRepository;

    public FtpService(FtpProperties props) {
        this.props = props;
    }

    public List<String> listDirectoriesInWatchFolder() throws NetworkError, NoConnection, LoginDenied, ListingFailed {

        ftpsRepository = new FtpsRepository();

        ftpsRepository.connect(props.getHost(), props.getPort(), props.getUser(), props.getPassword());

        FTPFile[] tFolders = ftpsRepository.listDirectories(props.getWatchDirectory());
        List<String> directoryNames = new ArrayList<>();

        directoryNames.add(".");

        for (FTPFile f : tFolders) {
            directoryNames.add(f.getName());
        }

        ftpsRepository.disconnect();

        return directoryNames;
    }

    public StringBuilder uploadFichier(FileToUpload fileToUpload) throws IOException, NetworkError, NoConnection, LoginDenied, UploadFailed, IncorrectFile, DirectoryNotFound {
        StringBuilder msgOk = new StringBuilder();

        ftpsRepository = new FtpsRepository();

        ftpsRepository.connect(props.getHost(), props.getPort(), props.getUser(), props.getPassword());

        for (MultipartFile file : fileToUpload.getFiles()) {
            ftpsRepository.uploadFile(file.getInputStream(), "watch/" + fileToUpload.getDestinationFolder(), file.getOriginalFilename());
            msgOk.append(file.getOriginalFilename()).append("<br />");
        }

        ftpsRepository.disconnect();

        return msgOk;
    }
}