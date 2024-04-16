package ru.netology.cloudstorage.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.netology.cloudstorage.CloudStorageApplicationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryTest extends CloudStorageApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsername() {
        var user = userRepository.findByUsername("test");
        assertTrue(user.isPresent());
        assertEquals(user.get().getUsername(), "test");
    }
}