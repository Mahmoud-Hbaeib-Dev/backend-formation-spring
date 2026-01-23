package com.formation.app.config;

import com.formation.app.security.JwtAuthenticationFilter;
import com.formation.app.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration de sécurité Spring Security
 * Configuration dual : JWT pour /api/** et Session pour /admin/**
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;
    
    /**
     * Bean pour encoder les mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * AuthenticationProvider pour l'authentification
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * SecurityFilterChain pour l'API REST (/api/**) - JWT Authentication
     */
    @Bean
    @org.springframework.core.annotation.Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
            .securityMatcher("/api/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // IMPORTANT: L'ordre compte ! Les règles les plus spécifiques doivent être en premier
                .requestMatchers("/api/auth/**").permitAll() // Endpoints d'authentification publics
                .requestMatchers("/api/diagnostic/**").permitAll() // Endpoints de diagnostic publics
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/etudiants/**").hasAnyRole("ADMIN", "FORMATEUR", "ETUDIANT")
                .requestMatchers("/api/formateurs/**").hasAnyRole("ADMIN", "FORMATEUR")
                .requestMatchers("/api/cours/**").hasAnyRole("ADMIN", "FORMATEUR", "ETUDIANT")
                .requestMatchers("/api/inscriptions/**").hasAnyRole("ADMIN", "FORMATEUR", "ETUDIANT")
                .requestMatchers("/api/seances/**").hasAnyRole("ADMIN", "FORMATEUR", "ETUDIANT")
                .requestMatchers("/api/notes/**").hasAnyRole("ADMIN", "FORMATEUR", "ETUDIANT") // Les étudiants peuvent voir leurs notes
                .requestMatchers("/api/statistiques/rapport-notes/**").hasAnyRole("ADMIN", "FORMATEUR", "ETUDIANT") // Les étudiants peuvent télécharger leur rapport
                .requestMatchers("/api/statistiques/**").hasAnyRole("ADMIN", "FORMATEUR")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * SecurityFilterChain pour l'interface Admin (/admin/**) - Session Authentication
     */
    @Bean
    @org.springframework.core.annotation.Order(3)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**", "/login", "/logout")
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/login/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
            )
            .authenticationProvider(authenticationProvider());
        
        return http.build();
    }
    
    /**
     * SecurityFilterChain pour H2 Console (prioritaire - doit être le premier)
     * L'ordre des @Bean est important, celui-ci doit être évalué en premier
     */
    @Bean
    @org.springframework.core.annotation.Order(1)
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/h2-console/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );
        
        return http.build();
    }
    
    /**
     * SecurityFilterChain par défaut pour les autres routes (sans securityMatcher pour gérer tout le reste)
     * IMPORTANT: Ne doit PAS intercepter /api/** car c'est géré par apiSecurityFilterChain
     */
    @Bean
    @org.springframework.core.annotation.Order(4)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/test", "/actuator/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/", "/error")
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );

        return http.build();
    }
}
