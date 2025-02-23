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
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        try {
            // Создаем директорию, если она не существует
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                logger.info("Создание директории для загрузки: {}", uploadPath);
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя файла
            String originalFileName = file.getOriginalFilename();
            String fileExtension = ".tmp"; // расширение по умолчанию

            if (originalFileName != null && !originalFileName.isEmpty()) {
                int lastDotIndex = originalFileName.lastIndexOf(".");
                if (lastDotIndex > 0) {
                    fileExtension = originalFileName.substring(lastDotIndex);
                }
            }

            String fileName = UUID.randomUUID().toString() + fileExtension;
            logger.info("Сохранение файла с именем: {}", fileName);

            // Сохраняем файл
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            logger.info("Файл успешно сохранен: {}", filePath);

            // Возвращаем относительный путь к файлу
            return "/uploads/" + fileName;
        } catch (IOException e) {
            logger.error("Ошибка при сохранении файла: {}", e.getMessage(), e);
            throw e;
        }
    }
} 