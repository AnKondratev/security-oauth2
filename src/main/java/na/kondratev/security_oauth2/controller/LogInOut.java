package na.kondratev.security_oauth2.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class LogInOut {

    @GetMapping("/")
    public String getHomepage() {
        return "start";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
