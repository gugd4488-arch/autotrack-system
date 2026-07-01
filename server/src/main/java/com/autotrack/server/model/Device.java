package com.autotrack.server.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String deviceId;
    
    private String name;
    private String model;
    private String osVersion;
    
    @Column(columnDefinition = "TEXT")
    private String lastScript;
    
    private Double lastLatitude;
    private Double lastLongitude;
    private LocalDateTime lastUpdate;
    
    private Boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}
