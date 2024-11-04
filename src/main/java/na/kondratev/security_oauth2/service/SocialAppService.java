package na.kondratev.security_oauth2.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import na.kondratev.security_oauth2.logger.GlobalLogger;
import na.kondratev.security_oauth2.model.Users;
import na.kondratev.security_oauth2.repository.UsersRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

@Service
@AllArgsConstructor
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UsersRepository userRepository;
    private final GlobalLogger logger;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User;
        try {
            oAuth2User = delegate.loadUser(userRequest);
        } catch (Exception e) {
            logger.logInfo(userRequest.getClientRegistration().getRegistrationId());
            throw e;
        }

        Integer githubId = oAuth2User.getAttribute("id");
        String username = oAuth2User.getAttribute("name");
        String login = oAuth2User.getAttribute("login");

        if (username == null) {
            logger.logError("Authentication failed: Username is null.", new RuntimeException());
        }

        Users user = userRepository.findByLogin(login);
        if (user == null) {
            String randomPassword = generateRandomPassword();
            user = Users.builder()
                    .username(username)
                    .login(login)
                    .githubId(githubId)
                    .password(randomPassword)
                    .role("USER")
                    .build();
            userRepository.save(user);
            logger.logInfo("New user created: " + username);
        } else {
            logger.logInfo("User already exists: " + username);
        }

        return oAuth2User;
    }

    public String generateRandomPassword() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS,
                        CharacterPredicates.DIGITS,
                        CharacterPredicates.ASCII_UPPERCASE_LETTERS)
                .build();

        return generator.generate(10);
    }
}
