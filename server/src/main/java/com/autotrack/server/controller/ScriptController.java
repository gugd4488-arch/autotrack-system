package com.autotrack.server.controller;

import com.autotrack.server.model.Script;
import com.autotrack.server.model.ScriptResult;
import com.autotrack.server.repository.ScriptRepository;
import com.autotrack.server.repository.ScriptResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {
    
    @Autowired
    private ScriptRepository scriptRepository;
    
    @Autowired
    private ScriptResultRepository scriptResultRepository;
    
    @GetMapping
    public List<Script> getAllScripts() {
        return scriptRepository.findAll();
    }
    
    @GetMapping("/{deviceId}")
    public List<Script> getPendingScripts(@PathVariable String deviceId) {
        return scriptRepository.findByTargetDeviceAndExecutedFalseAndEnabledTrue(deviceId);
    }
    
    @PostMapping
    public Script createScript(@RequestBody Script script) {
        script.setCreatedAt(LocalDateTime.now());
        script.setUpdatedAt(LocalDateTime.now());
        return scriptRepository.save(script);
    }
    
    @PostMapping("/result")
    public ResponseEntity<ScriptResult> reportResult(@RequestBody ScriptResult result) {
        result.setReportedAt(LocalDateTime.now());
        ScriptResult saved = scriptResultRepository.save(result);
        
        // Mark script as executed
        scriptRepository.findById(result.getScriptId()).ifPresent(script -> {
            script.setExecuted(true);
            scriptRepository.save(script);
        });
        
        return ResponseEntity.ok(saved);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScript(@PathVariable Long id) {
        scriptRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
