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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.EditFileNameRequest;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.service.StorageService;

import java.io.IOException;
import java.util.List;

import static ru.netology.cloudstorage.util.Constant.FILENAME_PARAM;
import static ru.netology.cloudstorage.util.Constant.LIMIT_PARAM;

@RequiredArgsConstructor
@RestController
public class StorageController {

    private final StorageService storageService;

    @GetMapping("/list")
    public List<FileListResponse> getAllFiles(@RequestParam(LIMIT_PARAM) Integer limit) {
        return storageService.getFileList(limit);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam(FILENAME_PARAM) String filename) {
        byte[] file = storageService.downloadFile(filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam(FILENAME_PARAM) String filename,
                                        MultipartFile file) throws IOException {
        storageService.uploadFile(filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam(FILENAME_PARAM) String filename) {
        storageService.deleteFile(filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping(value = "/file")
    public ResponseEntity<?> editFileName(@RequestParam(FILENAME_PARAM) String filename,
                                          @RequestBody EditFileNameRequest editFileNameRequest) {
        storageService.editFileName(filename, editFileNameRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
