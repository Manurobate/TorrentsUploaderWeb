package fr.robate.torrentuploader.model;

import lombok.Data;

import java.io.File;

@Data
public class FileToUpload {

    String destinationFolder;

    File file;
}
