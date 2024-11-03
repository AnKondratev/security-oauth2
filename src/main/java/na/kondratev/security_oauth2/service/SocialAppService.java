package na.kondratev.security_oauth2.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import na.kondratev.security_oauth2.model.Users;
import na.kondratev.security_oauth2.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsersRepository userRepository;
    private final LoggingFilter loggingFilter;
    private static final Logger logger = LoggerFactory.getLogger(SocialAppService.class);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User;
        try {
            oAuth2User = delegate.loadUser(userRequest);
        } catch (Exception e) {
            loggingFilter.logAuthenticationFailure(userRequest.getClientRegistration().getRegistrationId());
            throw e;
        }

        Integer githubId = oAuth2User.getAttribute("id");
        String username = oAuth2User.getAttribute("name");
        String login = oAuth2User.getAttribute("login");

        if (username == null) {
            loggingFilter.logAuthenticationFailure("Username is null");
            throw new RuntimeException("Authentication failed: Username is null.");
        }

        Users user = userRepository.findByLogin(login);
        if (user == null) {
            user = Users.builder()
                    .username(username)
                    .login(login)
                    .githubId(githubId)
                    .role("USER")
                    .build();
            userRepository.save(user);
            logger.info("New user created: {}", username);
        } else {
            logger.info("User already exists: {}", username);
        }

        return oAuth2User;
    }

}