package com.company.uploadfiles.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    @Test
    void decompressImage_shouldReturnTrue() throws IOException {
        byte[] originalImg = new byte[1000];

        Random r = new Random();
        r.nextBytes(originalImg);

        byte[] compressedImg = FileUtils.compressImage(originalImg);
        byte[] decompressedImg = FileUtils.decompressImage(compressedImg);

        assertArrayEquals(originalImg, decompressedImg);
    }
}