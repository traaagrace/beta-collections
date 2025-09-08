package service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author: aobei.bian
 * @date: 2025/3/23 22:14
 * @description:
 */
@Service
@Slf4j
public class ObsidianService {

    // 指定 Obsidian 笔记的存储路径
    private static final String OBISIDIAN_DIRECTORY = "D:\\Users\\aobei.bian\\Documents\\Obsidian Vault\\test\\";
    public ResponseEntity<Map<String, String>> addNote(Map<String, String> request) {
        // String title = request.get("title");
        String content = request.get("content");

//        if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Title and content cannot be empty!"));
//        }
        String title = "first-node";
        String fileName = title.replaceAll("[^a-zA-Z0-9_-]", "_") + ".md";
        File directory = new File(OBISIDIAN_DIRECTORY);

        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (!dirCreated) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to create directory!"));
            }
        }

        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            return ResponseEntity.ok(Map.of("message", "Note added successfully!"));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to add note!"));
        }
    }
}
