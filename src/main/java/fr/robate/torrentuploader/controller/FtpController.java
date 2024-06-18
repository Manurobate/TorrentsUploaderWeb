package fr.robate.torrentuploader.controller;

import fr.robate.torrentuploader.Exception.FtpException;
import fr.robate.torrentuploader.model.FileToUpload;
import fr.robate.torrentuploader.service.FtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
public class FtpController {

    @Autowired
    private FtpService ftpService;


    @GetMapping("/")
    public String displayIndex(Model model) throws FtpException {

        FileToUpload fileModel = new FileToUpload();
        model.addAttribute("fileToUpload", fileModel);

        List<String> watchFolders = ftpService.listDirectoriesInWatchFolder();
        model.addAttribute("watchFolders", watchFolders);

        return "index";
    }

    @PostMapping("/upload")
    public ModelAndView uploadTorrent(Model model, @ModelAttribute FileToUpload file) throws FtpException, IOException {

        
        String filename = file.getFile().getOriginalFilename();

        if (filename.toLowerCase().endsWith(".torrent")) {
            ftpService.uploadFichier(file.getFile(), file.getDestinationFolder());
            model.addAttribute("msg", "Fichier uploadé : " + filename);
        } else {
            model.addAttribute("msg", "Fichier doit etre .torrent");
        }


        return new ModelAndView("redirect:/");
    }
}
