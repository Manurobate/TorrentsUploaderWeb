package fr.robate.torrentuploader;

import fr.robate.torrentuploader.configuration.FtpProperties;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class FtpsRepositoryTest {
    private static FtpServer ftpServer;

    @Autowired
    private FtpProperties ftpProperties;

    @Autowired
    private FtpsRepository ftpsRepository;

    private static final String ftpHomeDir = "ftproot";

    @BeforeAll
    public static void setUp(@Autowired FtpProperties ftpProperties) throws Exception {
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
        user.setHomeDirectory(ftpHomeDir);

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        um.save(user);
        serverFactory.setUserManager(um);

        ftpServer = serverFactory.createServer();
        ftpServer.start();

        // Create the FTP home directory
        new File(ftpHomeDir).mkdirs();

        log.debug("FTP server started on port {}", ftpProperties.getPort());
    }

    @AfterAll
    public static void tearDown() {
        if (ftpServer != null) {
            ftpServer.stop();
        }
        // Clean up the FTP home directory
        deleteDirectory(new File(ftpHomeDir));
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    @Test
    void testConnectAndDisconnect() {
        try {
            ftpsRepository.connect(ftpProperties.getHost(), ftpProperties.getPort(), ftpProperties.getUser(), ftpProperties.getPassword());
            // Assuming there's a method isConnected to check the connection status
            assertTrue(ftpsRepository.isConnected(), "Should be connected to the FTP server");
            ftpsRepository.disconnect();
            assertFalse(ftpsRepository.isConnected(), "Should be disconnected from the FTP server");
        } catch (Exception e) {
            fail("Exception thrown during connect/disconnect test: " + e.getMessage());
        }
    }

    @Test
    void testListDirectories() {

        String watchDirectory = ftpHomeDir + "/" + ftpProperties.getWatchDirectory();

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
    }

    @Test
    void testListDirectoriesWithoutconnection() {
        String watchDirectory = ftpHomeDir + "/" + ftpProperties.getWatchDirectory();

        new File(watchDirectory).mkdir();

        Exception exception = assertThrows(NoConnection.class, () -> ftpsRepository.listDirectories(ftpProperties.getWatchDirectory()));

        assertSame(exception.getClass(), NoConnection.class);
    }
}