package fr.robate.torrentuploader.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);

        if (throwable != null) {
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append(element.toString()).append("\n");
            }
            model.addAttribute("trace", stackTrace.toString());
        } else {
            model.addAttribute("trace", "Aucune trace disponible.");
        }

        return "error";
    }
}
