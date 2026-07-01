package com.autotrack.server.repository;

import com.autotrack.server.model.ScriptResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptResultRepository extends JpaRepository<ScriptResult, Long> {
}
