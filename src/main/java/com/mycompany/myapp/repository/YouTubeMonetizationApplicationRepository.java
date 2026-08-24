package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MonetizationApplicationStatus;
import com.mycompany.myapp.domain.YouTubeMonetizationApplication;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YouTubeMonetizationApplicationRepository extends JpaRepository<YouTubeMonetizationApplication, Long> {
    @EntityGraph(attributePaths = { "channel", "assignment", "assignment.emailAccount", "user", "reviewedBy" })
    Page<YouTubeMonetizationApplication> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "channel", "assignment", "assignment.emailAccount", "user", "reviewedBy" })
    Page<YouTubeMonetizationApplication> findByStatus(MonetizationApplicationStatus status, Pageable pageable);

    @EntityGraph(attributePaths = { "channel", "assignment", "assignment.emailAccount", "user", "reviewedBy" })
    Optional<YouTubeMonetizationApplication> findDetailedById(Long id);

    boolean existsByChannelIdAndStatusIn(Long channelId, java.util.Collection<MonetizationApplicationStatus> statuses);

    boolean existsByChannelIdAndStatus(Long channelId, MonetizationApplicationStatus status);
}
