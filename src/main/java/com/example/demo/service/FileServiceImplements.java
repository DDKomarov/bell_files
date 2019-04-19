package com.example.demo.service;

import com.example.demo.model.UserFile;
import com.example.demo.repository.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImplements implements FileService {

    @Value("${upload.path}")
    private String uploadPath;

    private final UserFileRepository fileRepository;


    @Autowired
    public FileServiceImplements(UserFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public List<String> findAllFiles() {
        return fileRepository.getAllOriginalName();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void uploadFile(MultipartFile file) {
        if (file == null ||file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("Argument is null");
        }
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String resultFilename = createUniqueFileName(file.getOriginalFilename());

        try {
            file.transferTo(new File(uploadPath + "/" + resultFilename));
        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException("File or path not found!", e.getCause());
        }

        UserFile userFile = new UserFile();
        userFile.setOriginalName(file.getOriginalFilename());
        userFile.setFileName(resultFilename);
        userFile.setDownloadCount(0);
        fileRepository.save(userFile);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ResponseEntity downloadFile(UserFile userFile) {
        if (userFile == null) {
            throw new IllegalArgumentException("File not found!");
        }
        File file = new File(uploadPath + "/" + userFile.getFileName());
        userFile.setDownloadCount(userFile.getDownloadCount() + 1);
        fileRepository.save(userFile);
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("File or path not found!", e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteFile(String fileId) {
        Optional<UserFile> optionalUserFile = fileRepository.findById(Long.valueOf(fileId));

        optionalUserFile.ifPresent((file) -> {
            UserFile fileFromDB = optionalUserFile.get();
            File fileFromDisk = new File(uploadPath + "/" + fileFromDB.getFileName());
            if (!fileFromDisk.delete()) {
                throw new RuntimeException("The file was not deleted!");
            }
            fileRepository.delete(fileFromDB);
        });
    }

    /**
     * Сформировать уникальное имя файла
     *
     * @param originalFileName оригинальное имя файла
     * @return уникальное имя файла
     */
    private String createUniqueFileName(String originalFileName) {
        String uuidFile = UUID.randomUUID().toString();
        return uuidFile + "." + originalFileName;
    }
}
