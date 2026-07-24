package com.company.uploadfiles;

import com.company.uploadfiles.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileData, Long> {
    Optional<FileData> findByName(String name);
}
