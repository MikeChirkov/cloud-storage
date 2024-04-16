package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.repository.AuthTokenInMemoryRepository;
import ru.netology.cloudstorage.util.Constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.cloudstorage.TestData.USERNAME;

class AuthTokenServiceTest extends CloudStorageApplicationTests {

    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    AuthTokenInMemoryRepository authTokenInMemoryRepository;

    @Test
    void testAuthToken() {
        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        var token = authTokenService.generatedAuthToken(authentication);
        authTokenInMemoryRepository.putAuthToken(USERNAME, token);

        var claims = authTokenService.getClaims(token);
        var username = String.valueOf(claims.get(Constant.USERNAME));
        var isValidToken = authTokenService.isValidAuthToken(token);

        assertEquals(username, USERNAME);
        assertTrue(isValidToken);
    }

}