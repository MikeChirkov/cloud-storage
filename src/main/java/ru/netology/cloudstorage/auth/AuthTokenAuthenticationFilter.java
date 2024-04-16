package ru.netology.cloudstorage.auth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.service.AuthTokenService;

import java.io.IOException;
import java.util.Optional;

import static ru.netology.cloudstorage.util.Constant.AUTH_HEADER;
import static ru.netology.cloudstorage.util.Constant.BEARER;
import static ru.netology.cloudstorage.util.Constant.USERNAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthTokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorizationKey = request.getHeader(AUTH_HEADER);
        log.info("AuthTokenFilter: " + authorizationKey);
        if (Optional.ofNullable(authorizationKey).isPresent() && authorizationKey.startsWith(BEARER)) {
            authorizationKey = authorizationKey.replace(BEARER, "").trim();
            try {
                if (authTokenService.isValidAuthToken(authorizationKey)) {
                    var claims = authTokenService.getClaims(authorizationKey);
                    var username = String.valueOf(claims.get(USERNAME));
                    Authentication authentication = new UsernamePasswordAuthentication(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/login");
    }
}

