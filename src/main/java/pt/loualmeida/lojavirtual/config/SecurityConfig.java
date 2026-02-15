package pt.loualmeida.lojavirtual.config;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pt.loualmeida.lojavirtual.view.LoginView;

import static com.vaadin.flow.spring.security.VaadinSecurityConfigurer.vaadin;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
        );

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/login")
        );

        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );

        http.with(vaadin(), v -> v
                .loginView(LoginView.class)
        );

        return http.build();
    }

    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // Remove o prefixo "ROLE_" padrão do Spring Security
        // Agora @RolesAllowed("ADMIN") vai procurar por "ADMIN" e não "ROLE_ADMIN"
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
