package com.company.uploadfiles;

import com.company.uploadfiles.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {
    @Mock
    private FileService fileService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FileController(fileService)).build();
    }

    @Test
    void uploadImage_shouldReturnUploadedBytes() throws Exception {
        // Given
        byte[] bytes = {53, 34, 53, 76, 23, 46, 65};
        String description = "description test";
        String mockResponse = "Some String";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "test.png", "image/png", bytes);

        when(fileService.uploadImage(any(MultipartFile.class), eq(description))).thenReturn(mockResponse);

        // When
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/v1/upload")
                                .file(mockMultipartFile)
                                .param("description", description))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String contentResponse = result.getResponse().getContentAsString();
        assertEquals(mockResponse, contentResponse);
    }

    @Test
    void downloadImage_shouldReturnDownloadedBytes() throws Exception {
        //Given
        byte[] bytes = {43, 53, 53, 23, 54};
        String filename = "randImg";

        when(fileService.downloadImage(filename)).thenReturn(bytes);

        //When
        byte[] result = mockMvc.perform(get("/api/v1/download/{filename}", filename))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        //Then
        assertArrayEquals(result, bytes);
    }
}