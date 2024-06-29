package com.smarttech.SmartRH.AppUtils.FileUpload;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
    public static String saveFile(MultipartFile multipartFile)
            throws IOException {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
        String fileName = multipartFile.getOriginalFilename();
        String fileType = multipartFile.getContentType();

        if(!fileType.contains("application/pdf") && !fileType.contains("image/")) throw new IOException("Forbidden file type");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileCode = RandomStringUtils.randomAlphanumeric(8);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode + "-" + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }

        return fileCode + "-" + fileName;
    }
    public static boolean deleteFile(String filename)
            throws IOException {
        try {
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
            if (!Files.exists(uploadPath)) {
                throw new IOException("No Directory Found");
            }
            Path file = uploadPath.resolve(filename);
            if (!Files.exists(file)) {
                throw new IOException("No File Found");
            }
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new IOException("Error: " + e.getMessage());
        }
    }
}