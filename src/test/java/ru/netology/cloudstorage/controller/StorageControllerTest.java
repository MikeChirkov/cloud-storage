package ru.netology.cloudstorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.dto.EditFileNameRequest;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.service.StorageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cloudstorage.TestData.FILENAME;
import static ru.netology.cloudstorage.TestData.FILENAME_2;
import static ru.netology.cloudstorage.TestData.FILESIZE;
import static ru.netology.cloudstorage.TestData.USERNAME;
import static ru.netology.cloudstorage.util.Constant.FILENAME_PARAM;
import static ru.netology.cloudstorage.util.Constant.LIMIT_PARAM;

class StorageControllerTest extends CloudStorageApplicationTests {

    private static final String FILE_ENDPOINT = "/file";
    private static final String LIST_FILE_ENDPOINT = "/list";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StorageService storageService;

    @Test
    void getAllFiles() throws Exception {
        setAuth();
        List<FileListResponse> list = List.of(FileListResponse.builder()
                .filename(FILENAME)
                .size(FILESIZE)
                .build());

        when(storageService.getFileList(any())).thenReturn(list);

        var result = mockMvc.perform(get(LIST_FILE_ENDPOINT).param(LIMIT_PARAM, String.valueOf(1)))
                .andExpect(status().isOk())
                .andReturn();
        var resultArray = objectMapper.readValue(result.getResponse().getContentAsString(), FileListResponse[].class);

        assertEquals(resultArray.length, 1);
        assertEquals(resultArray[0].getFilename(), list.get(0).getFilename());
        assertEquals(resultArray[0].getSize(), list.get(0).getSize());
    }

    @Test
    void getAllFilesException() throws Exception {
        unsetAuth();

        mockMvc.perform(get(LIST_FILE_ENDPOINT).param(LIMIT_PARAM, String.valueOf(1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void downloadFile() throws Exception {
        setAuth();
        byte[] bytes = FILENAME.getBytes();

        when(storageService.downloadFile(any())).thenReturn(bytes);

        var result = mockMvc.perform(get(FILE_ENDPOINT).param(FILENAME_PARAM, FILENAME))
                .andExpect(status().isOk())
                .andReturn();
        var resultBytes = result.getResponse().getContentAsByteArray();

        assertArrayEquals(resultBytes, bytes);
    }

    @Test
    void downloadFileException() throws Exception {
        unsetAuth();

        mockMvc.perform(get(FILE_ENDPOINT).param(FILENAME_PARAM, FILENAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadFile() throws Exception {
        setAuth();

        Mockito.doNothing().when(storageService).uploadFile(any(), any());

        mockMvc.perform(post(FILE_ENDPOINT).param(FILENAME_PARAM, FILENAME_2))
                .andExpect(status().isOk());

    }

    @Test
    void uploadFileException() throws Exception {
        unsetAuth();

        mockMvc.perform(post(FILE_ENDPOINT).param(FILENAME_PARAM, FILENAME_2))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteFile() throws Exception {
        setAuth();

        Mockito.doNothing().when(storageService).deleteFile(any());

        mockMvc.perform(delete(FILE_ENDPOINT).param(FILENAME_PARAM, FILENAME_2))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFileException() throws Exception {
        unsetAuth();

        mockMvc.perform(delete(FILE_ENDPOINT).param(FILENAME_PARAM, FILENAME_2))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editFileName() throws Exception {
        setAuth();

        Mockito.doNothing().when(storageService).editFileName(any(), any());
        var body = objectMapper.writeValueAsString(new EditFileNameRequest(FILENAME));

        mockMvc.perform(put(FILE_ENDPOINT)
                        .param(FILENAME_PARAM, FILENAME_2)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void editFileNameException() throws Exception {
        unsetAuth();

        var body = objectMapper.writeValueAsString(new EditFileNameRequest(FILENAME));

        mockMvc.perform(put(FILE_ENDPOINT)
                        .param(FILENAME_PARAM, FILENAME_2)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private void setAuth() {
        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void unsetAuth() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}