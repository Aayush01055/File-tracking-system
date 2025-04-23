package com.ftms.backend.repository;

import com.ftms.backend.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<File, String> {
    List<File> findByTitleContainingIgnoreCaseOrStatusContainingIgnoreCase(String title, String status);
}