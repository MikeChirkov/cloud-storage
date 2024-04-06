package ru.netology.cloudstorage.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AuthTokenInMemoryRepository {
    private final Map<String, String> authTokenMap = new ConcurrentHashMap<>();

    public void putAuthToken(String username, String authToken) {
        authTokenMap.put(username, authToken);
    }

    public void removeAuthTokenByUsername(String username) {
        authTokenMap.remove(username);
    }

    public Optional<String> getAuthTokenByUsername(String username) {
        return Optional.ofNullable(authTokenMap.get(username));
    }
}
