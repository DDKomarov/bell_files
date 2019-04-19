package com.example.demo.controller;

import com.example.demo.model.UserFile;
import com.example.demo.service.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FileService fileService;

    private final UserFile userFile = new UserFile(6L,
            "5b8dd15e-d19f-4154-89bb-14dbf9bee4a6.dima.txt",
            "dima.txt",
            0);

    /**
     * Тестирует метод возращающий список файлов
     * */
    @Test
    public void showAllFiles() throws Exception {
        List<String> files = new ArrayList<>();
        files.add(userFile.getOriginalName());
        when(fileService.findAllFiles()).thenReturn(files);

        mvc.perform(get("/files")).andExpect(status().isOk())
                .andExpect(model().attribute("files", files));

    }

    /**
     * Тестирует метод сохранения файлов на сервер
     * */
    @Test
    public void uploadFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "dima.txt",
                "text/plain", "dima".getBytes());

        mvc.perform(fileUpload("/files").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", "/files"));
        verify(fileService).uploadFile(multipartFile);
    }

    /**
     * Тестриует метод скачивания файла
     * */
    @Test
    public void downloadFile() throws Exception {
        String pathname = "src/test/resources/" + userFile.getFileName();
        File file = new File(pathname);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);

        when(fileService.downloadFile(userFile)).thenReturn(responseEntity);

        mvc.perform(get("/files/6")).andExpect(status().isOk());

    }

    /**
     * Тестирует метод удаления файла
     * */
    @Test
    public void deleteFile() throws Exception {
        mvc.perform(delete("/files/6")).andExpect(status().isOk());
        verify(fileService).deleteFile("6");
    }

    /**
     * Тестирует метод удаления со строковом символом место числа */
    @Test
    public void deleteFileWithSymbolIdTest() throws Exception {
        doThrow(IllegalArgumentException.class).when(fileService).deleteFile("a");
        mvc.perform(delete("/files/b")).andExpect(status().isNotFound());
    }

    /**
     * Тестирует метод удаления с несущестующим id
     * */
    @Test
    public void deleteFileWithWromnIdTest() throws Exception {
        doThrow(IllegalArgumentException.class).when(fileService).deleteFile("-1");
        mvc.perform(delete("/files/-1")).andExpect(status().isNotFound());
    }
}