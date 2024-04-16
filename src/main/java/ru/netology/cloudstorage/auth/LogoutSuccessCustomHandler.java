package ru.netology.cloudstorage.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.netology.cloudstorage.repository.AuthTokenInMemoryRepository;
import ru.netology.cloudstorage.service.AuthTokenService;

import java.io.IOException;
import java.util.Optional;

import static ru.netology.cloudstorage.util.Constant.AUTH_HEADER;
import static ru.netology.cloudstorage.util.Constant.BEARER;
import static ru.netology.cloudstorage.util.Constant.USERNAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogoutSuccessCustomHandler extends
        HttpStatusReturningLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    AuthTokenInMemoryRepository authTokenRepository;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        var authorizationKey = request.getHeader(AUTH_HEADER);
        if (Optional.ofNullable(authorizationKey).isPresent() && authorizationKey.startsWith(BEARER)) {
            authorizationKey = authorizationKey.replace(BEARER, "").trim();
            var claims = authTokenService.getClaims(authorizationKey);
            var username = String.valueOf(claims.get(USERNAME));

            log.info("User {} logout. Token: {}", username, authorizationKey);

            authTokenRepository.removeAuthTokenByUsername(username);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        super.onLogoutSuccess(request, response, authentication);
    }
}
