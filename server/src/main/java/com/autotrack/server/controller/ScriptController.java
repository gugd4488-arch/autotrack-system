package com.autotrack.server.controller;

import com.autotrack.server.model.Script;
import com.autotrack.server.model.ScriptResult;
import com.autotrack.server.repository.ScriptRepository;
import com.autotrack.server.repository.ScriptResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/scripts")
@CrossOrigin(origins = "*")
public class ScriptController {
    
    @Autowired
    private ScriptRepository scriptRepository;
    
    @Autowired
    private ScriptResultRepository scriptResultRepository;
    
    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> getAllScripts(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        // 支持分页参数
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Script> scripts = scriptRepository.findAll(pageable);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(scripts);
        }
        // 不带分页参数时返回所有脚本
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(scriptRepository.findAll());
    }
    
    @GetMapping(value = "/{deviceId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Script>> getPendingScripts(@PathVariable String deviceId) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(scriptRepository.findByTargetDeviceAndExecutedFalseAndEnabledTrue(deviceId));
    }
    
    @PostMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<Script> createScript(@RequestBody Script script) {
        script.setCreatedAt(LocalDateTime.now());
        script.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(scriptRepository.save(script));
    }
    
    @PostMapping(value = "/result", produces = "application/json;charset=UTF-8")
    public ResponseEntity<ScriptResult> reportResult(@RequestBody ScriptResult result) {
        result.setReportedAt(LocalDateTime.now());
        ScriptResult saved = scriptResultRepository.save(result);
        
        // Mark script as executed
        scriptRepository.findById(result.getScriptId()).ifPresent(script -> {
            script.setExecuted(true);
            scriptRepository.save(script);
        });
        
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(saved);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScript(@PathVariable Long id) {
        scriptRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}



