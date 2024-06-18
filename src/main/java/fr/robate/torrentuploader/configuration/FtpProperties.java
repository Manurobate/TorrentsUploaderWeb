package fr.robate.torrentuploader.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {

    String host;

    int port;

    String user;

    String password;

    String watchDirectory;
}
