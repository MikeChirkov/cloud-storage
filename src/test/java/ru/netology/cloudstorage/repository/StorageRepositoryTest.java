package ru.netology.cloudstorage.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.entity.Storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.cloudstorage.TestData.FILENAME;
import static ru.netology.cloudstorage.TestData.FILESIZE;
import static ru.netology.cloudstorage.TestData.USERNAME;

class StorageRepositoryTest extends CloudStorageApplicationTests {

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        var user = userRepository.findByUsername(USERNAME).get();
        storageRepository.save(
                new Storage(FILENAME, FILESIZE, FILENAME.getBytes(), user));
    }

    @AfterEach
    void tearDown() {
        storageRepository.deleteAll();
    }

    @Test
    void findAllByUser() {
        var user = userRepository.findByUsername(USERNAME).get();

        var storage = storageRepository.findAllByUser(user, Limit.of(3));

        assertFalse(storage.isEmpty());
        assertEquals(storage.size(), 1);
    }

    @Test
    void findByUserAndFileName() {
        var user = userRepository.findByUsername(USERNAME).get();

        var storage = storageRepository.findByUserAndFileName(user, FILENAME);

        assertTrue(storage.isPresent());
        assertEquals(storage.get().getFileName(), FILENAME);
        assertEquals(storage.get().getFileSize(), FILESIZE);
        assertEquals(storage.get().getUser().getUsername(), user.getUsername());
    }

    @Test
    void deleteByUserAndFileName() {
        var user = userRepository.findByUsername(USERNAME).get();

        storageRepository.deleteByUserAndFileName(user, FILENAME);
        var storage = storageRepository.findByUserAndFileName(user, FILENAME);

        assertFalse(storage.isPresent());
    }
}