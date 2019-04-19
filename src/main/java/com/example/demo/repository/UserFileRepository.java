package com.example.demo.repository;

import com.example.demo.model.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserFileRepository extends JpaRepository<UserFile,Long> {

    @Query("select u.originalName from UserFile u")
    List<String> getAllOriginalName();
}
