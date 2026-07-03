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
        
        // 如果设备不存在，自动创建
        Device device = deviceRepository.findByDeviceId(position.getDeviceId())
            .orElseGet(() -> {
                Device newDevice = new Device();
                newDevice.setDeviceId(position.getDeviceId());
                newDevice.setName("Device_" + position.getDeviceId().substring(0, Math.min(8, position.getDeviceId().length())));
                newDevice.setActive(true);
                return deviceRepository.save(newDevice);
            });
        
        // Update device last position
        device.setLastLatitude(position.getLatitude());
        device.setLastLongitude(position.getLongitude());
        device.setLastUpdate(LocalDateTime.now());
        deviceRepository.save(device);
        
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/{deviceId}")
    public List<Position> getPositions(@PathVariable String deviceId) {
        return positionRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }
}
