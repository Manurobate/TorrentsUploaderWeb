package fr.robate.torrentuploader.service;

import fr.robate.torrentuploader.Exception.FtpException;
import fr.robate.torrentuploader.configuration.FtpProperties;
import fr.robate.torrentuploader.repository.FtpsRepository;
import lombok.Data;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class FtpService {

    @Autowired
    private FtpProperties props;

    @Autowired
    private FtpsRepository ftpsRepository;

    public List<String> listDirectoriesInWatchFolder() throws FtpException {

        ftpsRepository.connect(props.getHost(), props.getPort(), props.getUser(), props.getPassword());

        FTPFile[] tFolders = ftpsRepository.listDirectories(props.getWatchDirectory());
        List<String> directoryNames = new ArrayList<>();

        for (FTPFile f : tFolders) {
            directoryNames.add(f.getName());
        }

        ftpsRepository.disconnect();

        return directoryNames;
    }

    public void uploadFichier(MultipartFile file, String destFolder) throws FtpException, IOException {
        ftpsRepository.connect(props.getHost(), props.getPort(), props.getUser(), props.getPassword());

        ftpsRepository.uploadFile(file.getInputStream(), "watch/" + destFolder, file.getOriginalFilename());

        ftpsRepository.disconnect();
    }
}
