package com.ftms.backend.controller;

import com.ftms.backend.entity.File;
import com.ftms.backend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @GetMapping("/{id}")
    public ResponseEntity<File> getFile(@PathVariable String id, @RequestHeader("User-Id") String userId) {
        return fileRepository.findById(id)
                .map(file -> ResponseEntity.ok(file))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<File> createFile(@RequestBody File file, @RequestHeader("User-Id") String userId) {
        if (file.getTitle() == null || file.getTitle().trim().isEmpty() ||
                file.getStatus() == null || file.getStatus().trim().isEmpty() ||
                file.getCurrentOfficer() == null || file.getCurrentOfficer().trim().isEmpty()) {

            return ResponseEntity.badRequest().body(null);
        }
        file.setCreatedBy(userId);
        file.setTimestamp(LocalDateTime.now());
        File savedFile = fileRepository.save(file);
        return ResponseEntity.ok(savedFile);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<File> updateFile(
            @PathVariable String id,
            @RequestBody File updates,
            @RequestHeader("User-Id") String userId) {
        return fileRepository.findById(id)
                .map(file -> {
                    if (updates.getTitle() != null) file.setTitle(updates.getTitle());
                    if (updates.getStatus() != null) file.setStatus(updates.getStatus());
                    if (updates.getCurrentOfficer() != null) file.setCurrentOfficer(updates.getCurrentOfficer());
                    if (updates.getCourseCode() != null) file.setCourseCode(updates.getCourseCode());
                    if (updates.getExamSession() != null) file.setExamSession(updates.getExamSession());
                    file.setTimestamp(LocalDateTime.now());
                    File updatedFile = fileRepository.save(file);
                    return ResponseEntity.ok(updatedFile);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFiles(@RequestParam String query, @RequestHeader("User-Id") String userId) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Search query cannot be empty");
            }
            List<File> files = fileRepository.findByTitleContainingIgnoreCaseOrStatusContainingIgnoreCase(query, query);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}