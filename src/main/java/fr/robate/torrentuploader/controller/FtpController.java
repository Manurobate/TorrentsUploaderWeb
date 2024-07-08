package fr.robate.torrentuploader.controller;

import fr.robate.torrentuploader.configuration.FtpProperties;
import fr.robate.torrentuploader.model.FilesToUpload;
import fr.robate.torrentuploader.service.FtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

        FilesToUpload filesToUpload = new FilesToUpload();
        model.addAttribute("filesToUpload", filesToUpload);
        model.addAttribute("version", ftpProperties.getVersion());

        try {
            List<String> watchFolders = ftpService.listDirectoriesInWatchFolder();
            model.addAttribute("watchFolders", watchFolders);
        } catch (Exception e) {
            model.addAttribute("msgError", e.getMessage());
            log.error(e.getMessage(), e);
        }

        return "index";
    }

    @PostMapping("/upload")
    public ModelAndView uploadTorrent(RedirectAttributes redirectAttributes, @ModelAttribute FilesToUpload filesToUpload) {

        StringBuilder msgError = ftpService.checkFilestoUpload(filesToUpload);

        if (!msgError.isEmpty())
            redirectAttributes.addFlashAttribute("msgError", msgError);
        else {
            StringBuilder msgOk = new StringBuilder();

            try {
                msgOk = ftpService.uploadFichier(filesToUpload);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            if (!msgOk.isEmpty())
                redirectAttributes.addFlashAttribute("msgOk", msgOk);
        }
        
        return new ModelAndView("redirect:/");
    }
}