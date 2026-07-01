package com.autotrack.server.controller;

import com.autotrack.server.model.Position;
import com.autotrack.server.model.Device;
import com.autotrack.server.repository.PositionRepository;
import com.autotrack.server.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {
    
    @Autowired
    private PositionRepository positionRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @PostMapping
    public ResponseEntity<Position> reportPosition(@RequestBody Position position) {
        position.setServerTime(LocalDateTime.now());
        Position saved = positionRepository.save(position);
        
        // Update device last position
        deviceRepository.findByDeviceId(position.getDeviceId()).ifPresent(device -> {
            device.setLastLatitude(position.getLatitude());
            device.setLastLongitude(position.getLongitude());
            device.setLastUpdate(LocalDateTime.now());
            deviceRepository.save(device);
        });
        
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/{deviceId}")
    public List<Position> getPositions(@PathVariable String deviceId) {
        return positionRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }
}
