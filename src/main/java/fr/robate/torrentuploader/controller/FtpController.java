package fr.robate.torrentuploader.controller;

import fr.robate.torrentuploader.model.FileToUpload;
import fr.robate.torrentuploader.service.FtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
public class FtpController {

    @Autowired
    private FtpService ftpService;


    @GetMapping("/")
    public String displayIndex(Model model) {

        FileToUpload fileModel = new FileToUpload();
        model.addAttribute("fileToUpload", fileModel);

        try {
            List<String> watchFolders = ftpService.listDirectoriesInWatchFolder();
            model.addAttribute("watchFolders", watchFolders);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return "index";
    }

    @PostMapping("/upload")
    public ModelAndView uploadTorrent(Model model, @ModelAttribute FileToUpload file) {


        String filename = file.getFile().getOriginalFilename();

        if (filename.toLowerCase().endsWith(".torrent")) {
            try {
                ftpService.uploadFichier(file.getFile(), file.getDestinationFolder());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            model.addAttribute("msg", "Fichier uploadé : " + filename);
        } else {
            model.addAttribute("msg", "Fichier doit etre .torrent");
        }


        return new ModelAndView("redirect:/");
    }
}
