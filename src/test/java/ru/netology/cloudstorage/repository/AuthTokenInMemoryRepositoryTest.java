package ru.netology.cloudstorage.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.service.AuthTokenService;
import ru.netology.cloudstorage.util.Constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.cloudstorage.TestData.USERNAME;

class AuthTokenInMemoryRepositoryTest extends CloudStorageApplicationTests {

    @Autowired
    AuthTokenInMemoryRepository authTokenInMemoryRepository;
    @Autowired
    AuthTokenService authTokenService;

    @BeforeEach
    void init() {
        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        var token = authTokenService.generatedAuthToken(authentication);
        authTokenInMemoryRepository.putAuthToken(USERNAME, token);
    }

    @Test
    void testAuthTokenInMemory() {
        var authToken = authTokenInMemoryRepository.getAuthTokenByUsername(USERNAME);

        assertTrue(authToken.isPresent());

        var claims = authTokenService.getClaims(authToken.get());
        var username = String.valueOf(claims.get(Constant.USERNAME));

        assertEquals(username, USERNAME);
    }

    @Test
    void removeAuthTokenByUsername() {
        authTokenInMemoryRepository.removeAuthTokenByUsername(USERNAME);
        var authToken = authTokenInMemoryRepository.getAuthTokenByUsername(USERNAME);

        assertFalse(authToken.isPresent());
    }

}