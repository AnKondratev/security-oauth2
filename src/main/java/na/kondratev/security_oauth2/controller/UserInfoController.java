package na.kondratev.security_oauth2.controller;

import lombok.AllArgsConstructor;
import na.kondratev.security_oauth2.service.SocialAppService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class UserInfoController {

    SocialAppService appService;

    @GetMapping("/user")
    public String userAttributes(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        model.addAttribute("name", oAuth2User.getAttribute("name"));
        model.addAttribute("login", oAuth2User.getAttribute("login"));
        model.addAttribute("id", oAuth2User.getAttribute("id"));
        model.addAttribute("email", oAuth2User.getAttribute("email"));
        return "user";
    }
}