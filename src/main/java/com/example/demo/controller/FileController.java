package com.example.demo.controller;

import com.example.demo.model.UserFile;
import com.example.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class FileController {
    @Value("${upload.path}")
    private String uploadPath;

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Отобразить все файлы в системе.
     *
     * @param model модель
     * @return страница со списком файлов
     */
    @GetMapping("/files")
    public String showAllFiles(Model model) {
        List<String> files = fileService.findAllFiles();
        model.addAttribute("readAccess", false);
        model.addAttribute("files", files);

        return "files";
    }

    /**
     * Добавляет файл в систему
     *
     * @param file файл
     * @return страница со списком файлов
     */
    @PostMapping("/files")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.uploadFile(file);
        return "redirect:/files";
    }

    /**
     * Загружает файл из системы
     *
     * @param userFile файл
     * @return ResponseEntity сформированный ответ контроллера
     */
    @GetMapping("/download/{userFile:.+}")
    public ResponseEntity downloadFile (@PathVariable("userFile") UserFile userFile) {
        return fileService.downloadFile(userFile);
    }

    /**
     * Удаляет файл из системы
     *
     * @param file id файла
     * @return страница со списком файлов
     */
    @DeleteMapping("/delete/{file}")
    public String deleteFile(@PathVariable("file") String file) {
        fileService.deleteFile(file);
        return "redirect:/files";
    }

}
