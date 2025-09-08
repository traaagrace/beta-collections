package controller;

/**
 * @author: aobei.bian
 * @date: 2025/3/23 21:25
 * @description:
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.ObsidianService;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/obsidian")
public class ObsidianController {

    @Autowired
    private ObsidianService obsidianService;

    @PostMapping("/add-note")
    public void saveNote(@RequestBody Map<String, String> noteData) {
        obsidianService.addNote(noteData);
    }
}
