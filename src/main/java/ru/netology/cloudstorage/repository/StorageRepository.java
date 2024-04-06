package ru.netology.cloudstorage.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entity.Storage;
import ru.netology.cloudstorage.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
    List<Storage> findAllByUser(User user, Limit limit);

    Optional<Storage> findByUserAndFileName(User user, String fileName);

    void deleteByUserAndFileName(User user, String fileName);
}
