package com.knowledgevault.storage.repositories;

import com.knowledgevault.storage.entities.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<StoredFile, UUID> {
    Optional<StoredFile> findByStorageKey(String storageKey);
}
