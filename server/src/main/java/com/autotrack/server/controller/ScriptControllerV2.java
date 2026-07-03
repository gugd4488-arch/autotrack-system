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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v2/scripts")
@CrossOrigin(origins = "*")
public class ScriptControllerV2 {
    
    @Autowired
    private ScriptRepository scriptRepository;
    
    @Autowired
    private ScriptResultRepository scriptResultRepository;
    
    // 获取所有脚本（分页）
    @GetMapping
    public ResponseEntity<?> getAllScripts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean enabled) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Script> scripts;
        
        if (search != null && !search.isEmpty()) {
            scripts = scriptRepository.findByNameContainingOrDescriptionContaining(search, search, pageable);
        } else if (enabled != null) {
            scripts = scriptRepository.findByEnabled(enabled, pageable);
        } else {
            scripts = scriptRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(scripts);
    }
    
    // 获取脚本详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getScript(@PathVariable Long id) {
        Optional<Script> script = scriptRepository.findById(id);
        return script.isPresent() 
            ? ResponseEntity.ok(script.get())
            : ResponseEntity.notFound().build();
    }
    
    // 创建脚本
    @PostMapping
    public ResponseEntity<?> createScript(@RequestBody Script script) {
        script.setCreatedAt(LocalDateTime.now());
        script.setUpdatedAt(LocalDateTime.now());
        Script saved = scriptRepository.save(script);
        return ResponseEntity.ok(saved);
    }
    
    // 更新脚本
    @PutMapping("/{id}")
    public ResponseEntity<?> updateScript(@PathVariable Long id, @RequestBody Script scriptDetails) {
        Optional<Script> scriptOpt = scriptRepository.findById(id);
        if (!scriptOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Script script = scriptOpt.get();
        if (scriptDetails.getName() != null) script.setName(scriptDetails.getName());
        if (scriptDetails.getCode() != null) script.setCode(scriptDetails.getCode());
        if (scriptDetails.getDescription() != null) script.setDescription(scriptDetails.getDescription());
        if (scriptDetails.getEnabled() != null) script.setEnabled(scriptDetails.getEnabled());
        script.setUpdatedAt(LocalDateTime.now());
        
        Script updated = scriptRepository.save(script);
        return ResponseEntity.ok(updated);
    }
    
    // 删除脚本
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScript(@PathVariable Long id) {
        if (!scriptRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        scriptRepository.deleteById(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "脚本已删除"));
    }
    
    // 批量启用/禁用
    @PatchMapping("/batch/enable")
    public ResponseEntity<?> batchEnable(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) request.get("ids");
        Boolean enabled = (Boolean) request.get("enabled");
        
        ids.forEach(id -> {
            Optional<Script> script = scriptRepository.findById(id);
            if (script.isPresent()) {
                Script s = script.get();
                s.setEnabled(enabled);
                scriptRepository.save(s);
            }
        });
        
        return ResponseEntity.ok(Collections.singletonMap("message", ids.size() + "个脚本已更新"));
    }
    
    // 删除所有脚本
    @DeleteMapping
    public ResponseEntity<?> deleteAllScripts() {
        scriptRepository.deleteAll();
        return ResponseEntity.ok(Collections.singletonMap("message", "所有脚本已删除"));
    }
    
    // 执行脚本
    @PostMapping("/{id}/execute")
    public ResponseEntity<?> executeScript(@PathVariable Long id, @RequestBody(required = false) Map<String, String> params) {
        Optional<Script> scriptOpt = scriptRepository.findById(id);
        if (!scriptOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Script script = scriptOpt.get();
        String deviceId = params != null ? params.get("deviceId") : "unknown";
        
        // 创建执行记录
        ScriptResult result = new ScriptResult();
        result.setScriptId(id);
        result.setDeviceId(deviceId);
        result.setStatus("执行中");
        result.setExecutedAt(LocalDateTime.now());
        ScriptResult saved = scriptResultRepository.save(result);
        
        // TODO: 实际执行脚本逻辑
        
        return ResponseEntity.ok(Collections.singletonMap("executionId", saved.getId()));
    }
    
    // 获取脚本执行历史
    @GetMapping("/{id}/history")
    public ResponseEntity<?> getScriptHistory(@PathVariable Long id) {
        List<ScriptResult> results = scriptResultRepository.findByScriptIdOrderByReportedAtDesc(id);
        return ResponseEntity.ok(results);
    }
    
    // 统计数据
    @GetMapping("/stats/summary")
    public ResponseEntity<?> getStatsSummary() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalScripts", scriptRepository.count());
        stats.put("enabledScripts", scriptRepository.countByEnabled(true));
        stats.put("totalExecutions", scriptResultRepository.count());
        return ResponseEntity.ok(stats);
    }
}
