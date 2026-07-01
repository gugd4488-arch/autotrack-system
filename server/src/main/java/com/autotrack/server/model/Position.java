package com.autotrack.server.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions")
@Data
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String deviceId;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double speed;
    private Double course;
    private Double accuracy;
    
    private LocalDateTime timestamp;
    private LocalDateTime serverTime = LocalDateTime.now();
    
    @Column(columnDefinition = "TEXT")
    private String attributes;
}
