package na.kondratev.security_oauth2.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import na.kondratev.security_oauth2.model.CustomOAuth2User;
import na.kondratev.security_oauth2.model.Users;
import na.kondratev.security_oauth2.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsersRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(SocialAppService.class);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Integer githubId = oAuth2User.getAttribute("id");
        String username = oAuth2User.getAttribute("name");

        Users user = userRepository.findByUsername(username);
        if (user == null) {
            user = Users.builder()
                    .username(username)
                    .githubId(githubId)
                    .role("USER")
                    .build();
            userRepository.save(user);
            logger.info("New user created: {}", username);
        } else {
            logger.info("User already exists: {}", username);
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new CustomOAuth2User(authorities, oAuth2User);
    }
}