package na.kondratev.security_oauth2.config;

import na.kondratev.security_oauth2.service.SocialAppService;
import org.hibernate.property.access.internal.PropertyAccessMapImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SocialAppService socialAppService) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/profile/**", "/login", "/logout", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user_access/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
//
//                .oauth2ResourceServer(config -> config.jwt(jwt -> {
//                    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//                    jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
//                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
//                    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//                    JwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//                    customJwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("groups");
//                    customJwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
//
//                    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(token ->
//                            Stream.concat(jwtGrantedAuthoritiesConverter.convert(token).stream(),
//                                            customJwtGrantedAuthoritiesConverter.convert(token).stream())
//                                    .toList());
//                }))
//
//
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(socialAppService)
                        )
                        .defaultSuccessUrl("/profile/user")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        OidcUserService oidcUserService = new OidcUserService();
        return userRequest -> {
            OidcUser oidcUser = oidcUserService.loadUser(userRequest);
            List<GrantedAuthority> authorities = Stream.concat(oidcUser.getAuthorities().stream(),
                            oidcUser.getClaimAsStringList("groups").stream()
                                    .filter(authority -> authority.startsWith("ROLE_"))
                                    .map(SimpleGrantedAuthority::new))
                    .toList();
            return new DefaultOidcUser(authorities,
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    "preferred_username");
        };
    }
}