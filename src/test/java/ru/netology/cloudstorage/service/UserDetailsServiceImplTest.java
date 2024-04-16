package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.netology.cloudstorage.TestData.USERNAME;
import static ru.netology.cloudstorage.TestData.USERNAME_WRONG;

class UserDetailsServiceImplTest extends CloudStorageApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername() {
        var user = userRepository.findByUsername(USERNAME);
        var userDetail = userDetailsService.loadUserByUsername(USERNAME);

        assertEquals(userDetail.getUsername(), user.get().getUsername());
        assertEquals(userDetail.getPassword(), user.get().getPassword());
    }

    @Test
    void loadUserByUsernameException() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(USERNAME_WRONG));
    }
}