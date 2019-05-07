package com.example.demo.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name  = "user_file")
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название файла
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * Оригинальное название файла
     */
    @Column(name = "original_name")
    private String originalName;

    /**
     * Количество скачиваний
     */
    @Column(name = "download_count")
    private Integer downloadCount;

}
