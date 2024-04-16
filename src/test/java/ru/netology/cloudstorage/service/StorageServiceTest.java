package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.dto.EditFileNameRequest;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.entity.Storage;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.InternalServerException;
import ru.netology.cloudstorage.repository.StorageRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.cloudstorage.TestData.FILENAME;
import static ru.netology.cloudstorage.TestData.FILENAME_2;
import static ru.netology.cloudstorage.TestData.FILENAME_WRONG;
import static ru.netology.cloudstorage.TestData.FILESIZE;
import static ru.netology.cloudstorage.TestData.USERNAME;

class StorageServiceTest extends CloudStorageApplicationTests {

    @Autowired
    StorageService storageService;
    @Autowired
    StorageRepository storageRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        var user = userRepository.findByUsername(USERNAME).get();
        storageRepository.save(
                new Storage(FILENAME, FILESIZE, FILENAME.getBytes(), user));

        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        storageRepository.deleteAll();
    }

    @Test
    void uploadFile() {
        byte[] bytes = FILENAME_2.getBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile(FILENAME_2, bytes);
        storageService.uploadFile(FILENAME_2, mockMultipartFile);

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findByUserAndFileName(user, FILENAME_2);

        assertTrue(storage.isPresent());
        assertEquals(storage.get().getFileName(), FILENAME_2);
        assertEquals(storage.get().getUser().getUsername(), user.getUsername());
    }

    @Test
    void uploadFileError() {
        assertThrows(InternalServerException.class, () -> storageService.uploadFile(FILENAME, null));
    }

    @Test
    void getFileList() {
        var fileList = storageService.getFileList(3);

        assertFalse(fileList.isEmpty());
        assertEquals(fileList.size(), 1);
        assertEquals(fileList.get(0).getFilename(), FILENAME);
        assertEquals(fileList.get(0).getSize(), FILESIZE);
    }

    @Test
    void downloadFile() {
        byte[] bytesActual = FILENAME.getBytes();

        var bytesExpected = storageService.downloadFile(FILENAME);

        assertTrue(bytesExpected.length > 0);
        assertArrayEquals(bytesExpected, bytesActual);
    }

    @Test
    void downloadFileError() {
        assertThrows(InputDataException.class, () -> storageService.downloadFile(FILENAME_WRONG));
    }

    @Test
    void deleteFile() {
        storageService.deleteFile(FILENAME);

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findByUserAndFileName(user, FILENAME);

        assertFalse(storage.isPresent());
    }

    @Test
    void deleteFileError() {
        assertThrows(InputDataException.class, () -> storageService.deleteFile(FILENAME_WRONG));
    }

    @Test
    void editFileName() {
        storageService.editFileName(FILENAME, new EditFileNameRequest(FILENAME_2));

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findByUserAndFileName(user, FILENAME_2);

        assertTrue(storage.isPresent());
        assertEquals(storage.get().getFileName(), FILENAME_2);
        assertEquals(storage.get().getUser().getUsername(), user.getUsername());
    }

    @Test
    void editFileNameError() {
        assertThrows(InputDataException.class, () -> storageService.editFileName(FILENAME_WRONG,
                new EditFileNameRequest(FILENAME_2)));
    }

}