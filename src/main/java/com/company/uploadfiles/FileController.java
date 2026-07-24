package com.company.uploadfiles;

import com.company.uploadfiles.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam(value = "image", required = true) MultipartFile file,
            @RequestParam(value = "description", required = true) String description
    ) throws IOException {
        String uploadImage = fileService.uploadImage(file, description);

        return ResponseEntity.ok(uploadImage);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadImage(@PathVariable("filename") String fileName) {
        byte[] imageData = fileService.downloadImage(fileName);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(imageData);
    }
}
