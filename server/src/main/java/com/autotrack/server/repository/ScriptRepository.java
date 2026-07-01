package com.autotrack.server.repository;

import com.autotrack.server.model.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Long> {
    List<Script> findByTargetDeviceAndExecutedFalseAndEnabledTrue(String targetDevice);
}
