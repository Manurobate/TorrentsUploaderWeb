package fr.robate.torrentuploader.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FileToUpload {

    String destinationFolder;

    List<MultipartFile> files;
}
