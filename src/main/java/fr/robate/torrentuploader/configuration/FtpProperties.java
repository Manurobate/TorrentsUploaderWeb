package fr.robate.torrentuploader.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {

    @Value("${project.version}")
    String version;

    String host;

    int port;

    String user;

    String password;

    String watchDirectory;
}
