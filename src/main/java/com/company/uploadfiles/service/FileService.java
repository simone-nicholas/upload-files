package com.company.uploadfiles.service;

import com.company.uploadfiles.FileRepository;
import com.company.uploadfiles.util.FileUtils;
import com.company.uploadfiles.model.FileData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository imageRepository) {
        this.fileRepository = imageRepository;
    }

    public String uploadImage(MultipartFile file, String description) throws IOException {
        FileData imageData = new FileData();
        imageData.setName(file.getOriginalFilename());
        imageData.setType(file.getContentType());
        imageData.setDescription(description);
        imageData.setSize(file.getSize());
        imageData.setUuid(UUID.randomUUID());
        imageData.setImageData(FileUtils.compressImage(file.getBytes()));

        fileRepository.save(imageData);
        return imageData.getName();
    }

    public byte[] downloadImage(String fileName) {
        Optional<FileData> dbImageData = fileRepository.findByName(fileName);

        try {
            byte[] images = FileUtils.decompressImage(dbImageData.get().getImageData());

            return images;
        } catch (NoSuchElementException e) {
            System.err.println("Error 404 :: " + e.getMessage());
        }

        return null;
    }
}
