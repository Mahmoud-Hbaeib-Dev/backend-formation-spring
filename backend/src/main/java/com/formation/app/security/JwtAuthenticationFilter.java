package com.formation.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre pour intercepter les requêtes et valider les JWT tokens
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Ignorer les endpoints publics (auth et diagnostic)
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || path.startsWith("/api/diagnostic/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        // Vérifier si le header Authorization existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraire le token (enlever "Bearer ")
        jwt = authHeader.substring(7);
        
        try {
            // Extraire le username depuis le token
            username = jwtTokenService.extractUsername(jwt);
            
            // Si le username existe et qu'il n'y a pas encore d'authentification
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Charger les UserDetails
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // Valider le token
                if (jwtTokenService.validateToken(jwt, userDetails)) {
                    // Créer l'authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Définir l'authentification dans le SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // En cas d'erreur, on continue sans authentification
            logger.error("Cannot set user authentication: {}", e);
        }
        
        filterChain.doFilter(request, response);
    }
}

