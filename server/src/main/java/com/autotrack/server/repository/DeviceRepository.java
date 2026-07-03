package com.autotrack.server.repository;

import com.autotrack.server.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);
    
    // 查询所有激活的设备
    List<Device> findByActiveTrue();
    
    // 查询所有不活跃的设备
    List<Device> findByActiveFalse();
}
