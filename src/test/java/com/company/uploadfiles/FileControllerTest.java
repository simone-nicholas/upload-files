package com.company.uploadfiles;

import com.company.uploadfiles.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {
    @InjectMocks
    private FileService fileService;

    @Test
    void uploadImage_shouldReturnUploadedBytes() {
        byte[] bytes = {53, 34, 53, 76, 23, 46, 65};

        // Given
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test".getBytes());

        // When

        // Then
    }
}