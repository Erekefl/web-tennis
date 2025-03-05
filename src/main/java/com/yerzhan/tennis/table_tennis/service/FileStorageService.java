package com.yerzhan.tennis.table_tennis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir);
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось создать директорию для загрузки файлов.", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        try {
            if (file.isEmpty()) {
                throw new IOException("Файл пуст");
            }

            // Генерируем уникальное имя файла
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Сохраняем файл
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Файл успешно сохранен: {}", targetLocation);

            // Возвращаем путь к файлу относительно /uploads/
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            logger.error("Ошибка при сохранении файла: {}", ex.getMessage());
            throw ex;
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg"; // Расширение по умолчанию
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
} 