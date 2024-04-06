package ru.netology.cloudstorage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.dto.ExceptionResponse;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.dto.LoginResponse;
import ru.netology.cloudstorage.repository.AuthTokenInMemoryRepository;
import ru.netology.cloudstorage.service.AuthTokenService;

import java.io.IOException;

import static ru.netology.cloudstorage.util.Constant.AUTH_HEADER;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenService authTokenService;
    private final AuthTokenInMemoryRepository authTokenRepository;
    private final UsernamePasswordAuthenticationProvider authenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        if (request.getHeader(AUTH_HEADER) == null) {
            var bodyJson = request.getReader().readLine();
            if (bodyJson != null) {
                var mapper = new ObjectMapper();
                var userDto = mapper.readValue(bodyJson, LoginRequest.class);
                var username = userDto.getLogin();
                var password = userDto.getPassword();
                try {
                    Authentication authentication = new UsernamePasswordAuthentication(username, password, null);
                    authentication = authenticationProvider.authenticate(authentication);
                    var authToken = authTokenService.generatedAuthToken(authentication);
                    authTokenRepository.putAuthToken(username, authToken);

                    log.info("User {} authentication. Token: {}", username, authToken);

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(mapper.writeValueAsString(new LoginResponse(authToken)));
                    response.getWriter().flush();
                } catch (BadCredentialsException | ObjectNotFoundException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(mapper.writeValueAsString(
                            new ExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage())));
                    response.getWriter().flush();
                }
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/login");
    }
}

