package com.autotrack.server.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "scripts")
@Data
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;
    
    private String description;
    private String targetDevice;
    
    private LocalDateTime executeAt;
    private Boolean executed = false;
    private Boolean enabled = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
