package com.autotrack.server.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/management")
@CrossOrigin(origins = "*")
public class ManagementController {
    
    // 系统信息
    @GetMapping("/system/info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        info.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
        info.put("processorCount", Runtime.getRuntime().availableProcessors());
        info.put("uptime", System.currentTimeMillis());
        return info;
    }
    
    // 日志管理
    @GetMapping("/logs")
    public Map<String, Object> getLogs(
            @RequestParam(defaultValue = "100") int limit) {
        Map<String, Object> logs = new HashMap<>();
        logs.put("limit", limit);
        logs.put("entries", new ArrayList<>());
        return logs;
    }
    
    // 配置管理
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("gpsPort", 5055);
        config.put("storageDir", "/data/scripts");
        config.put("maxScriptTimeout", 300000);
        config.put("scriptExecutionLimit", 100);
        return config;
    }
    
    // 更新配置
    @PutMapping("/config")
    public Map<String, Object> updateConfig(@RequestBody Map<String, Object> config) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "配置已更新");
        result.put("config", config);
        return result;
    }
    
    // 获取性能指标
    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("cpuUsage", Math.random() * 100);
        metrics.put("memoryUsage", Math.random() * 100);
        metrics.put("diskUsage", Math.random() * 100);
        metrics.put("networkIn", Math.random() * 1000000);
        metrics.put("networkOut", Math.random() * 1000000);
        metrics.put("timestamp", System.currentTimeMillis());
        return metrics;
    }
    
    // 用户管理
    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 1);
        admin.put("username", "admin");
        admin.put("role", "超级管理员");
        admin.put("lastLogin", System.currentTimeMillis());
        users.add(admin);
        
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("total", users.size());
        return result;
    }
    
    // 审计日志
    @GetMapping("/audit-log")
    public Map<String, Object> getAuditLog(
            @RequestParam(defaultValue = "50") int limit) {
        List<Map<String, Object>> logs = new ArrayList<>();
        
        Map<String, Object> entry = new HashMap<>();
        entry.put("id", 1);
        entry.put("action", "script_executed");
        entry.put("user", "admin");
        entry.put("timestamp", System.currentTimeMillis());
        entry.put("details", "执行了脚本 #1");
        logs.add(entry);
        
        Map<String, Object> result = new HashMap<>();
        result.put("logs", logs);
        result.put("total", logs.size());
        return result;
    }
    
    // 备份管理
    @PostMapping("/backup")
    public Map<String, Object> createBackup() {
        Map<String, Object> backup = new HashMap<>();
        backup.put("backupId", UUID.randomUUID().toString());
        backup.put("timestamp", System.currentTimeMillis());
        backup.put("status", "已创建");
        return backup;
    }
    
    @GetMapping("/backups")
    public Map<String, Object> listBackups() {
        List<Map<String, Object>> backups = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        result.put("backups", backups);
        return result;
    }
    
    // 恢复备份
    @PostMapping("/restore/{backupId}")
    public Map<String, Object> restoreBackup(@PathVariable String backupId) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "备份恢复中...");
        result.put("backupId", backupId);
        return result;
    }
    
    // 数据库统计
    @GetMapping("/database/stats")
    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("database", "autotrack");
        stats.put("status", "正常");
        stats.put("tables", Arrays.asList("scripts", "devices", "positions", "script_results"));
        stats.put("dataSize", "5 MB");
        return stats;
    }
    
    // 清理过期数据
    @PostMapping("/cleanup")
    public Map<String, Object> cleanupOldData(@RequestParam(defaultValue = "30") int days) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "清理 " + days + " 天前的数据");
        result.put("deletedRecords", 0);
        result.put("freedSpace", "0 MB");
        return result;
    }
}
