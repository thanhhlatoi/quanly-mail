package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.WorkLog;
import com.mycompany.myapp.domain.WorkLogStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @EntityGraph(attributePaths = { "assignment", "assignment.emailAccount", "user", "channel" })
    Page<WorkLog> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "assignment", "assignment.emailAccount", "user", "channel" })
    Page<WorkLog> findByStatus(WorkLogStatus status, Pageable pageable);

    @EntityGraph(attributePaths = { "assignment", "assignment.emailAccount", "user", "channel" })
    Optional<WorkLog> findDetailedById(Long id);
}
