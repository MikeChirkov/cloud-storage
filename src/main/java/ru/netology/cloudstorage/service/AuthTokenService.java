package ru.netology.cloudstorage.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.repository.AuthTokenInMemoryRepository;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.util.Constant;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthTokenService {
    @Value("${jwt.signing.key}")
    private String signingKey;
    @Value("${jwt.key.expiration}")
    private Long tokenExpiration;

    private final UserRepository userRepository;
    private final AuthTokenInMemoryRepository authenticationRepository;
    private SecretKey key;

    private SecretKey generatedSecretKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    public String generatedAuthToken(Authentication authentication) {
        return Jwts.builder()
                .setClaims(
                        Map.of(
                                Constant.USERNAME, authentication.getName()))
                .setExpiration(new Date(new Date().getTime() + tokenExpiration))
                .setSubject(authentication.getName())
                .signWith(generatedSecretKey())
                .compact();
    }

    public Claims getClaims(String authToken) {
        return Jwts.parserBuilder()
                .setSigningKey(generatedSecretKey())
                .build()
                .parseClaimsJws(authToken)
                .getBody();
    }

    public boolean isValidAuthToken(String authToken) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(generatedSecretKey())
                .build()
                .parseClaimsJws(authToken)
                .getBody();

        var username = String.valueOf(claims.get(Constant.USERNAME));
        var user = userRepository.findByUsername(username);
        var tokenFromMemory = authenticationRepository.getAuthTokenByUsername(username);

        return claims.getExpiration().after(new Date())
                && user.isPresent()
                && tokenFromMemory.isPresent()
                && tokenFromMemory.get().equals(authToken);
    }

}

