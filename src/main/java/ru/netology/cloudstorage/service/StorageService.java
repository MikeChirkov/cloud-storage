package ru.netology.cloudstorage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.EditFileNameRequest;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.entity.Storage;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.InternalServerException;
import ru.netology.cloudstorage.repository.StorageRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageService {

    private final UserRepository userRepository;
    private final StorageRepository storageRepository;

    public void uploadFile(String filename, MultipartFile file) {
        try {
            var user = getUserFromSecurityContext();
            storageRepository.save(new Storage(filename, file.getSize(), file.getBytes(), user));
        } catch (InputDataException e){
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error upload file", e.getMessage());
        }
    }

    public List<FileListResponse> getFileList(Integer limit) {
        try {
            var user = getUserFromSecurityContext();
            var storages = storageRepository.findAllByUser(user, Limit.of(limit));
            return storages.stream()
                    .map(o -> new FileListResponse(o.getFileName(), o.getFileSize()))
                    .collect(Collectors.toList());
        } catch (InputDataException e){
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error getting file list", e.getMessage());
        }
    }

    public byte[] downloadFile(String filename) {
        try {
            var file = getFileByFilename(filename);
            return file.getFileContent();
        } catch (InputDataException e){
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error download file", e.getMessage());
        }
    }

    @Transactional
    public void deleteFile(String filename) {
        try {
            var user = getUserFromSecurityContext();
            getFileByFilename(filename);
            storageRepository.deleteByUserAndFileName(user, filename);
        } catch (InputDataException e){
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error delete file", e.getMessage());
        }
    }

    public void editFileName(String filename, EditFileNameRequest editFileNameRequest) {
        try {
            var file = getFileByFilename(filename);
            file.setFileName(editFileNameRequest.getFilename());
            storageRepository.save(file);
        } catch (InputDataException e){
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Error edit file", e.getMessage());
        }
    }

    private User getUserFromSecurityContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new InputDataException(String.format("User with username %s is not found", username));
        }
    }

    private Storage getFileByFilename(String filename) {
        var user = getUserFromSecurityContext();
        var file = storageRepository.findByUserAndFileName(user, filename);
        if (file.isPresent()) {
            return file.get();
        } else {
            throw new InputDataException(String.format("File with filename %s is not found", filename));
        }
    }
}
