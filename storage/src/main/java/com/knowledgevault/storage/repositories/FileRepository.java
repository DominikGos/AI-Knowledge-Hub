package com.knowledgevault.storage.repositories;

import com.knowledgevault.storage.entities.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<StoredFile, UUID> {
}
