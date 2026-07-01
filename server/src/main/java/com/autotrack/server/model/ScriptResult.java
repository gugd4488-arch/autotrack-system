package com.autotrack.server.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "script_results")
@Data
public class ScriptResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long scriptId;
    private String deviceId;
    
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String output;
    
    @Column(columnDefinition = "TEXT")
    private String error;
    
    private LocalDateTime executedAt;
    private LocalDateTime reportedAt = LocalDateTime.now();
}
