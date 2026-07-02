package com.autotrack.server.repository;

import com.autotrack.server.model.ScriptResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScriptResultRepository extends JpaRepository<ScriptResult, Long> {
    
    // 按脚本ID查询结果
    List<ScriptResult> findByScriptIdOrderByReportedAtDesc(Long scriptId);
    
    // 按设备ID查询结果
    List<ScriptResult> findByDeviceIdOrderByReportedAtDesc(String deviceId);
    
    // 查询所有结果按时间倒序
    List<ScriptResult> findAllByOrderByReportedAtDesc();
}
