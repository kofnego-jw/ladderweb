package at.ac.uibk.fiba.ladder3ca.webapp.service;

import at.ac.uibk.fiba.ladder3ca.business.security.AppRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    protected Customizer<LogoutConfigurer<HttpSecurity>> logoutConfiguration() {
        return logout -> {
            logout.logoutUrl("/public/api/v1/logout");
            logout.permitAll();
            logout.clearAuthentication(true);
            logout.deleteCookies("JSESSIONID");
            logout.invalidateHttpSession(true);
            logout.logoutSuccessHandler((request, response, authentication) -> {
                response.setStatus(HttpServletResponse.SC_OK);
                // response.setHeader("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\", \"executionContexts\"");
                try (OutputStream os = response.getOutputStream()) {
                    os.write("{\"msg\":\"logged out\"}".getBytes(StandardCharsets.UTF_8));
                    os.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
    }

    protected Customizer<ExceptionHandlingConfigurer<HttpSecurity>> exceptionHandlerConfiguration() {
        return exc -> {
            exc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        };
    }

    protected Customizer<SessionManagementConfigurer<HttpSecurity>> sessionManagementConfiguration() {
        return session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .maximumSessions(1)
                    .sessionRegistry(sessionRegistry());
        };
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(this.exceptionHandlerConfiguration())
                .cors().disable()
                .csrf().ignoringAntMatchers("/public/api/**").csrfTokenRepository(csrfTokenRepository())
                .and()
                .authorizeRequests(req ->
                        req.antMatchers("/*", "/assets/**", "/public/**").permitAll()
                                .antMatchers("/api/v1/admin/**").hasAnyAuthority(AppRole.ADMIN.getAuthority())
                                .antMatchers("/api/v1/**").hasAnyAuthority(AppRole.USER.getAuthority())
                                .anyRequest().authenticated()
                )
                .httpBasic()
                .and()
                .sessionManagement(sessionManagementConfiguration())
                .logout(this.logoutConfiguration());
        return http.build();
    }


}
