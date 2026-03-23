package com.example.cinema.api.infrastructure.security;

import com.example.cinema.api.infrastructure.security.exception.TokenValidationException;
import com.example.cinema.api.infrastructure.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

        private final TokenService tokenService;
        private final UserDetailsService userDetailsService;

        public SecurityFilter(TokenService tokenService, UserDetailsService userDetailsService) {
            this.tokenService = tokenService;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
                throws ServletException, IOException {

            String token = recoverToken(request);

            if (token != null) {
                try {
                    String username = tokenService.validateToken(token);
                    UserDetails user = userDetailsService.loadUserByUsername(username);

                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (TokenValidationException e) {
                    log.warn("Token inválido: {}", e.getMessage());
                    request.setAttribute("auth.error", e.getMessage());
                    SecurityContextHolder.clearContext();
                } catch (UsernameNotFoundException e) {
                    log.warn("Usuário do token não encontrado: {}", e.getMessage());
                    request.setAttribute("auth.error", "Usuário não encontrado");
                    SecurityContextHolder.clearContext();}
            }

            filterChain.doFilter(request, response);
        }

        private String recoverToken(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }
            return authHeader.substring(7);
        }
    }