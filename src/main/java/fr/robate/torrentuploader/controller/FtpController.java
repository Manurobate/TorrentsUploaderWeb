package fr.robate.torrentuploader.controller;

import fr.robate.torrentuploader.configuration.FtpProperties;
import fr.robate.torrentuploader.model.FileToUpload;
import fr.robate.torrentuploader.service.FtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class FtpController {

    private final FtpService ftpService;

    private final FtpProperties ftpProperties;

    public FtpController(FtpService ftpService, FtpProperties ftpProperties) {
        this.ftpService = ftpService;
        this.ftpProperties = ftpProperties;
    }

    @GetMapping("/")
    public String displayIndex(Model model) {

        FileToUpload fileModel = new FileToUpload();
        model.addAttribute("fileToUpload", fileModel);
        model.addAttribute("version", ftpProperties.getVersion());

        try {
            List<String> watchFolders = ftpService.listDirectoriesInWatchFolder();
            model.addAttribute("watchFolders", watchFolders);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return "index";
    }

    @PostMapping("/upload")
    public ModelAndView uploadTorrent(RedirectAttributes redirectAttributes, @ModelAttribute FileToUpload fileToUpload) {

        List<MultipartFile> files = fileToUpload.getFiles();
        StringBuilder message = new StringBuilder();

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();

            if (filename == null) {
                message.append("Nom de fichier null : ").append("<br>");
                continue;
            }

            if (filename.toLowerCase().endsWith(".torrent")) {
                try {
                    ftpService.uploadFichier(file, fileToUpload.getDestinationFolder());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                message.append("Fichier non torrent : ").append(filename).append("<br>");
            }
        }

        if (!message.isEmpty())
            redirectAttributes.addFlashAttribute("msg", message);

        return new ModelAndView("redirect:/");
    }
}