package fr.robate.torrentuploader;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Data
@SpringBootApplication
public class TorrentUploaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TorrentUploaderApplication.class, args);
    }

}
