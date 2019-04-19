package com.example.demo.service;

import com.example.demo.model.UserFile;
import com.example.demo.repository.UserFileRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileServiceTest {

    @Mock
    private FileService fileService;

    @MockBean
    private UserFileRepository fileRepository;
    private final UserFile userFile = new UserFile(6L,
            "5b8dd15e-d19f-4154-89bb-14dbf9bee4a6.dima.txt",
            "dima.txt",
            0);

    /**
     * Тестирует метод, возвращающего список имён файлов
     *
     * @throws Exception
     */
    @Test
    public void findAllFilesTest() {
        List<String> files = new ArrayList<>();
        files.add(userFile.getOriginalName());
        when(fileService.findAllFiles()).thenReturn(files);

        Assert.assertEquals(files, fileService.findAllFiles());
    }


    /**
     * Тестирует метод сохранения файла
     */

    @Test
    public void uploadFileTest() {
        MockMultipartFile multipartFile = new MockMultipartFile(
                userFile.getOriginalName(),
                userFile.getFileName(),
                MediaType.TEXT_PLAIN_VALUE,
                "dima".getBytes());
        fileService.uploadFile(multipartFile);
        doReturn(userFile)
                .when(fileRepository)
                .getOne(6L);

        Assert.assertEquals(userFile, fileRepository.getOne(6L));
        verify(fileService, times(1)).uploadFile(multipartFile);
    }


    /**
     * Тестирует загрузку null файла
     *
     * @throws IllegalArgumentException
     */
    @Test
    public void uploadFileTestNull() throws IllegalArgumentException {
        MockMultipartFile file = null;
        doThrow(IllegalArgumentException.class).when(fileService).uploadFile(file);
    }

    /**
     * Тестирует загрузку файла с @see UserFile#originalName равным null
     *
     * @throws IllegalArgumentException
     */
    @Test
    public void uploadFileWithOriginalFilenameEqualsNull() throws IllegalArgumentException {
        MockMultipartFile file = new MockMultipartFile(
                null,
                userFile.getFileName(),
                MediaType.TEXT_PLAIN_VALUE,
                "dima".getBytes());

        doThrow(IllegalArgumentException.class).when(fileService).uploadFile(file);
    }


    /**
     * Тестирует загрузку файла с @see UserFile#originalName равным пустой строке
     *
     * @throws IllegalArgumentException
     */
    @Test
    public void uploadFileWithOriginalFilenameIsEmpty() throws IllegalArgumentException {
        MockMultipartFile file = new MockMultipartFile(
                "",
                userFile.getFileName(),
                MediaType.TEXT_PLAIN_VALUE,
                "dima".getBytes());

        doThrow(IllegalArgumentException.class).when(fileService).uploadFile(file);
    }


    /**
     * Тестирует метод удаления файла
     */
    @Test
    public void deleteFileTest() {
        fileRepository.save(userFile);
        fileService.deleteFile("6");
        verify(fileService, times(1)).deleteFile("6");
        Assert.assertEquals(false, fileRepository.findById(6L).isPresent());
    }


    /**
     * Тестирует метод загрузки файла
     *
     * @throws FileNotFoundException
     */
    @Test
    public void downloadFileTest() throws FileNotFoundException {
        fileRepository.save(userFile);
        File file = new File("src/test/resources/47fb4801-10e9-49a7-a3c4-ffb34db0f1cc.test.txt");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
        when(fileService.downloadFile(userFile)).thenReturn(responseEntity);
        Assert.assertEquals(responseEntity, fileService.downloadFile(userFile));
    }
}