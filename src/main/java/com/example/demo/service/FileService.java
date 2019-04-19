package com.example.demo.service;


import com.example.demo.model.UserFile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    /**
     * Найти все файлы в системе
     *
     * @return список файлов
     */
    List<String> findAllFiles();

    /**
     * Добавить файл в систему
     *
     * @param file файл
     */
    void uploadFile(MultipartFile file);

    /**
     * Загрузить файл из системы
     *
     * @param userFile файл
     * @return ResponseEntity сформированный ответ для контроллера
     */
    ResponseEntity downloadFile(UserFile userFile);

    /**
     * Удалить файл
     *
     * @param fileId
     */
    void deleteFile(String fileId);
}
