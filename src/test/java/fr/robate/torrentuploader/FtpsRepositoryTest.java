package fr.robate.torrentuploader;

import fr.robate.torrentuploader.configuration.FtpProperties;
import fr.robate.torrentuploader.exception.NetworkError;
import fr.robate.torrentuploader.exception.NoConnection;
import fr.robate.torrentuploader.repository.FtpsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class FtpsRepositoryTest {
    private static FtpServer ftpServer;
    private static Path ftpHomeDir;

    @Autowired
    private FtpProperties ftpProperties;

    @Autowired
    private FtpsRepository ftpsRepository;

    @BeforeAll
    public static void setUp(@Autowired FtpProperties ftpProperties) throws Exception {
        ftpHomeDir = Files.createTempDirectory("ftproot");
        log.debug("Using temporary directory for FTP home: {}", ftpHomeDir.toString());

        // Set up the FTP server
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(ftpProperties.getPort());

        SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setKeystoreFile(new File("src/test/resources/ftps.jks"));
        ssl.setKeystorePassword("password");

        factory.setSslConfiguration(ssl.createSslConfiguration());
        factory.setImplicitSsl(false);

        serverFactory.addListener("default", factory.createListener());

        // Set up the user manager
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        UserManager um = userManagerFactory.createUserManager();
        BaseUser user = new BaseUser();
        user.setName(ftpProperties.getUser());
        user.setPassword(ftpProperties.getPassword());
        user.setHomeDirectory(ftpHomeDir.toString());

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        um.save(user);
        serverFactory.setUserManager(um);

        ftpServer = serverFactory.createServer();
        ftpServer.start();

        log.debug("Waiting 2 seconds to be sure the server is fully started");
        Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> true);

        log.debug("FTP server started on port {}", ftpProperties.getPort());
    }

    @AfterAll
    public static void tearDown() {
        if (ftpServer != null) {
            ftpServer.stop();
        }

        // Clean up the FTP home directory
        try {
            Files.walk(ftpHomeDir)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            log.error("Error cleaning up temporary FTP home directory", e);
        }
        log.debug("FTP server stopped and temporary directory cleaned up.");
    }

    @BeforeEach
    public void init() throws NetworkError {
        log.debug("Begin init test isConnected");

        // Ensure the repository is disconnected before each test
        try {
            if (ftpsRepository.isConnected()) {
                log.debug("FTP repository is already connected. Disconnecting...");
                ftpsRepository.disconnect();
                log.debug("Disconnected");
            } else {
                log.debug("Not connected");
            }
        } catch (Exception e) {
            log.warn("Error during init disconnect: {}", e.getMessage());
        }

        log.debug("End init test isConnected");
    }

    @Test
    void testConnectAndDisconnect() {
        log.debug("Begin testConnectAndDisconnect");

        try {
            ftpsRepository.connect(ftpProperties.getHost(), ftpProperties.getPort(), ftpProperties.getUser(), ftpProperties.getPassword());
            // Assuming there's a method isConnected to check the connection status
            assertTrue(ftpsRepository.isConnected(), "Should be connected to the FTP server");

            log.debug("Waiting 2 seconds before disconnect");
            Awaitility.await().atMost(2, TimeUnit.SECONDS).until(() -> true);

            ftpsRepository.disconnect();
            assertFalse(ftpsRepository.isConnected(), "Should be disconnected from the FTP server");
        } catch (Exception e) {
            fail("Exception thrown during connect/disconnect test: " + e.getMessage());
        }

        log.debug("End testConnectAndDisconnect");
    }

    @Test
    void testListDirectories() {
        log.debug("Begin testListDirectories");

        String watchDirectory = ftpHomeDir.resolve(ftpProperties.getWatchDirectory()).toString();

        try {
            ftpsRepository.connect(ftpProperties.getHost(), ftpProperties.getPort(), ftpProperties.getUser(), ftpProperties.getPassword());

            new File(watchDirectory).mkdir();

            // Create directories in the FTP home directory
            new File(watchDirectory + "/Films").mkdir();
            new File(watchDirectory + "/Series").mkdir();

            FTPFile[] directories = ftpsRepository.listDirectories(ftpProperties.getWatchDirectory());

            List<String> lstDirectories = new ArrayList<>();

            for (FTPFile f : directories) {
                lstDirectories.add(f.getName());
            }

            assertTrue(lstDirectories.contains("Films"), "Directory 'Films' should be listed");
            assertTrue(lstDirectories.contains("Series"), "Directory 'Series' should be listed");

            ftpsRepository.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during listDirectories test: " + e.getMessage());
        }

        log.debug("End testListDirectories");
    }

    @Test
    void testListDirectoriesWithoutConnection() {
        log.debug("Begin testListDirectoriesWithoutConnection");

        Exception exception = assertThrows(NoConnection.class, () -> ftpsRepository.listDirectories(ftpProperties.getWatchDirectory()));

        assertSame(exception.getClass(), NoConnection.class);

        log.debug("End testListDirectoriesWithoutConnection");
    }
}