package com.company.uploadfiles.service;

import com.company.uploadfiles.FileRepository;
import com.company.uploadfiles.model.FileData;
import com.company.uploadfiles.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @Test
    void uploadImage_shouldCompressAndSaveFile() throws IOException {
        //Given
        byte[] originalBytes = {54, 23, 23, 52, 53, 12};
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.png", "image/png", originalBytes
        );
        String description = "description test";

        // When
        String resultName = fileService.uploadImage(file, description);

        // Then
        assertEquals("test.png", resultName);
        verify(fileRepository).save(any(FileData.class));
    }

    @Test
    void downloadImage_shouldReturnDecompressedBytes() {
        String fileName = "test";

        // Given
        byte[] originalBytes = {54, 23, 23, 52, 53, 12};
        byte[] compressedBytes = FileUtils.compressImage(originalBytes);

        FileData fileD = new FileData();
        fileD.setName(fileName);
        fileD.setImageData(compressedBytes);

        when(fileRepository.findByName(fileName)).thenReturn(Optional.of(fileD));

        // When
        byte[] result = fileService.downloadImage(fileName);

        // Then
        assertArrayEquals(originalBytes, result);
    }
}