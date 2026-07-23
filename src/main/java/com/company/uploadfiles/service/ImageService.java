package com.company.uploadfiles.service;

import com.company.uploadfiles.ImageRepository;
import com.company.uploadfiles.util.ImageUtils;
import com.company.uploadfiles.model.ImageData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String uploadImage(MultipartFile file, String description) throws IOException {
        ImageData imageData = new ImageData();
        imageData.setName(file.getOriginalFilename());
        imageData.setType(file.getContentType());
        imageData.setDescription(description);
        imageData.setSize(file.getSize());
        imageData.setUuid(UUID.randomUUID());
        imageData.setImageData(ImageUtils.compressImage(file.getBytes()));

        imageRepository.save(imageData);
        return imageData.getName();
    }

    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = imageRepository.findByName(fileName);

        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());

        return images;
    }
}
