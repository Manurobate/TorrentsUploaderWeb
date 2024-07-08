package fr.robate.torrentuploader.service;

import fr.robate.torrentuploader.configuration.FtpProperties;
import fr.robate.torrentuploader.exception.*;
import fr.robate.torrentuploader.model.FilesToUpload;
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

    public StringBuilder checkFilestoUpload(FilesToUpload filesToUpload) {
        StringBuilder msgError = new StringBuilder();

        for (MultipartFile file : filesToUpload.getFiles()) {
            String filename = file.getOriginalFilename();

            if (filename == null) {
                msgError.append("Nom de fichier null <br />");
                continue;
            }

            if (!filename.toLowerCase().endsWith(".torrent")) {
                msgError.append("Mauvais format : ").append(filename).append("<br />");
            }
        }
        
        return msgError;
    }

    public StringBuilder uploadFichier(FilesToUpload filesToUpload) throws IOException, NetworkError, NoConnection, LoginDenied, UploadFailed, IncorrectFile, DirectoryNotFound {
        StringBuilder msgOk = new StringBuilder();

        ftpsRepository = new FtpsRepository();

        ftpsRepository.connect(props.getHost(), props.getPort(), props.getUser(), props.getPassword());

        for (MultipartFile file : filesToUpload.getFiles()) {
            ftpsRepository.uploadFile(file.getInputStream(), "watch/" + filesToUpload.getDestinationFolder(), file.getOriginalFilename());
            msgOk.append(file.getOriginalFilename()).append("<br />");
        }

        ftpsRepository.disconnect();

        return msgOk;
    }
}