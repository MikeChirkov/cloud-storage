package ru.netology.cloudstorage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.netology.cloudstorage.util.Constant.BEARER;
import static ru.netology.cloudstorage.util.Constant.USERNAME;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageService {

    private final UserRepository userRepository;
    private final StorageRepository storageRepository;
    private final AuthTokenService authTokenService;

    public void uploadFile(String authToken, String filename, MultipartFile file) throws IOException {
        try {
            var user = getUserByAuthToken(authToken);
            storageRepository.save(new Storage(filename, file.getSize(), file.getBytes(), user));
        } catch (Exception e) {
            throw new InternalServerException("Error upload file", e.getMessage());
        }
    }

    public List<FileListResponse> getFileList(String authToken, Integer limit) {
        try {
            var user = getUserByAuthToken(authToken);
            var storages = storageRepository.findAllByUser(user, Limit.of(limit));

            return storages.stream()
                    .map(o -> new FileListResponse(o.getFileName(), o.getFileSize()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerException("Error getting file list", e.getMessage());
        }

    }

    public byte[] downloadFile(String authToken, String filename) {
        try {
            var user = getUserByAuthToken(authToken);
            var file = storageRepository.findByUserAndFileName(user, filename);
            if (file.isPresent()) {
                return file.get().getFileContent();
            } else {
                throw new InputDataException(String.format("File with filename %s is not found", filename));
            }
        } catch (Exception e) {
            throw new InternalServerException("Error download file", e.getMessage());
        }

    }

    @Transactional
    public void deleteFile(String authToken, String filename) {
        try {
            var user = getUserByAuthToken(authToken);
            storageRepository.deleteByUserAndFileName(user, filename);
        } catch (Exception e) {
            throw new InternalServerException("Error delete file", e.getMessage());
        }
    }

    public void editFileName(String authToken, String filename, EditFileNameRequest editFileNameRequest) {
        try {
            var user = getUserByAuthToken(authToken);
            var file = storageRepository.findByUserAndFileName(user, filename);
            if (file.isPresent()) {
                file.get().setFileName(editFileNameRequest.getFilename());
                storageRepository.save(file.get());
            } else {
                throw new InputDataException(String.format("File with filename %s is not found", filename));
            }
        } catch (Exception e) {
            throw new InternalServerException("Error edit file", e.getMessage());
        }
    }

    private User getUserByAuthToken(String authToken) {
        if (authToken.startsWith(BEARER)) {
            var claims = authTokenService.getClaims(authToken.replace(BEARER, "").trim());
            var username = String.valueOf(claims.get(USERNAME));
            var user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                return user.get();
            } else {
                throw new InternalServerException(String.format("User with username %s is not found", username));
            }
        }
        return null;
    }
}
