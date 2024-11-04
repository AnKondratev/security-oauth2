package na.kondratev.security_oauth2.config;

import lombok.AllArgsConstructor;
import na.kondratev.security_oauth2.logger.GlobalLogger;
import na.kondratev.security_oauth2.service.SocialAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final GlobalLogger globalLogger; // добавляем логер

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SocialAppService socialAppService) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/profile/**", "/login", "/logout", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user_access/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(socialAppService)
                        )
                        .defaultSuccessUrl("/profile/user")
                        .successHandler((request, response, authentication) -> {
                            globalLogger.logInfo("User " + authentication.getName() + " logged in successfully.");
                            response.sendRedirect("/profile/user");
                        })
                        .failureHandler((request, response, exception) -> {
                            globalLogger.logError("Login failed: " + exception.getMessage(), exception);
                            response.sendRedirect("/login?error");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                globalLogger.logInfo("User " + authentication.getName() + " logged out successfully.");
                            }
                        })
                );

        return http.build();
    }

}