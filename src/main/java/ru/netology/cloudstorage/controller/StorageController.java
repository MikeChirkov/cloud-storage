package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.EditFileNameRequest;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.service.StorageService;

import java.io.IOException;
import java.util.List;

import static ru.netology.cloudstorage.util.Constant.AUTH_HEADER;
import static ru.netology.cloudstorage.util.Constant.FILENAME_HEADER;
import static ru.netology.cloudstorage.util.Constant.LIMIT_HEADER;

@RequiredArgsConstructor
@RestController
public class StorageController {

    private final StorageService storageService;

    @GetMapping("/list")
    public List<FileListResponse> getAllFiles(@RequestHeader(AUTH_HEADER) String authToken,
                                              @RequestParam(LIMIT_HEADER) Integer limit) {
        return storageService.getFileList(authToken, limit);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestHeader(AUTH_HEADER) String authToken,
                                                 @RequestParam(FILENAME_HEADER) String filename) {
        byte[] file = storageService.downloadFile(authToken, filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader(AUTH_HEADER) String authToken,
                                        @RequestParam(FILENAME_HEADER) String filename,
                                        MultipartFile file) throws IOException {
        storageService.uploadFile(authToken, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader(AUTH_HEADER) String authToken,
                                        @RequestParam(FILENAME_HEADER) String filename) {
        storageService.deleteFile(authToken, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping(value = "/file")
    public ResponseEntity<?> editFileName(@RequestHeader(AUTH_HEADER) String authToken,
                                          @RequestParam(FILENAME_HEADER) String filename,
                                          @RequestBody EditFileNameRequest editFileNameRequest) {
        storageService.editFileName(authToken, filename, editFileNameRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
