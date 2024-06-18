package fr.robate.torrentuploader.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileToUpload {

    String destinationFolder;

    MultipartFile file;
}
