package com.company.uploadfiles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UploadFilesEndToEndIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("uploadfiles_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepository fileRepository;

    @Test
    void uploadThenDownload_fullStackWithRealDatabase_returnsOriginalBytes() throws Exception {
        // Given
        byte[] originalBytes = new byte[2048];
        new Random().nextBytes(originalBytes);
        String filename = "integration-test-image.png";
        MockMultipartFile multipartFile =
                new MockMultipartFile("image", filename, "image/png", originalBytes);

        mockMvc.perform(multipart("/api/v1/upload")
                        .file(multipartFile)
                        .param("description", "foto di test end-to-end"))
                .andExpect(status().isOk());

        assertTrue(fileRepository.findByName(filename).isPresent(),
                "Il file dovrebbe essere stato persistito nel DB reale");

        // When
        MvcResult downloadResult = mockMvc.perform(get("/api/v1/download/{filename}", filename))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] downloadedBytes = downloadResult.getResponse().getContentAsByteArray();
        assertArrayEquals(originalBytes, downloadedBytes);
    }

    @Test
    void uploadThenDownload_withEmptyFile_returnsEmptyBytesWithoutError() throws Exception {
        // Given
        byte[] emptyBytes = new byte[0];
        String filename = "empty-file.png";
        MockMultipartFile multipartFile =
                new MockMultipartFile("image", filename, "image/png", emptyBytes);

        // When
        mockMvc.perform(multipart("/api/v1/upload")
                        .file(multipartFile)
                        .param("description", "file vuoto"))
                .andExpect(status().isOk());

        // Then
        MvcResult result = mockMvc.perform(get("/api/v1/download/{filename}", filename))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(0, result.getResponse().getContentAsByteArray().length);
    }

    @Test
    void download_withNonExistentFilename_returnsOkWithEmptyBody_currentBehavior() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/api/v1/download/{filename}", "file-che-non-esiste"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        byte[] body = result.getResponse().getContentAsByteArray();
        assertEquals(0, body.length,
                "Comportamento attuale: nessun errore esplicito, solo corpo vuoto. "
                        + "Da considerare come possibile miglioramento futuro (dovrebbe essere un 404).");
    }

    @Test
    void uploadTwiceWithSameName_thenDownload_throwsIncorrectResultSizeException() throws Exception {
        // Given
        String duplicateName = "duplicate.png";
        MockMultipartFile firstFile =
                new MockMultipartFile("image", duplicateName, "image/png", new byte[]{1, 2, 3});
        MockMultipartFile secondFile =
                new MockMultipartFile("image", duplicateName, "image/png", new byte[]{4, 5, 6});

        mockMvc.perform(multipart("/api/v1/upload").file(firstFile).param("description", "primo"))
                .andExpect(status().isOk());
        mockMvc.perform(multipart("/api/v1/upload").file(secondFile).param("description", "secondo"))
                .andExpect(status().isOk());

        // When / Then
        assertThrows(IncorrectResultSizeDataAccessException.class,
                () -> fileRepository.findByName(duplicateName));
    }
}