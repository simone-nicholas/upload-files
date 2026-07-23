package com.company.uploadfiles;

import com.company.uploadfiles.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findByName(String name);
}
