package com.autotrack.server.repository;

import com.autotrack.server.model.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findByTargetDeviceAndExecutedFalseAndEnabledTrue(String targetDevice);
    
    // 分页查询搜索
    org.springframework.data.domain.Page<Script> findByNameContainingOrDescriptionContaining(
        String name, String description, org.springframework.data.domain.Pageable pageable);
    
    // 按启用状态查询
    org.springframework.data.domain.Page<Script> findByEnabled(Boolean enabled, org.springframework.data.domain.Pageable pageable);
    
    // 统计启用的脚本
    long countByEnabled(Boolean enabled);
}
