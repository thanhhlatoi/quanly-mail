package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.YouTubeChannel;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YouTubeChannelRepository extends JpaRepository<YouTubeChannel, Long> {
    List<YouTubeChannel> findByAssignmentIdIn(List<Long> assignmentIds);
    Page<YouTubeChannel> findByUserId(Long userId, Pageable pageable);

    Page<YouTubeChannel> findByAssignmentId(Long assignmentId, Pageable pageable);

    Page<YouTubeChannel> findByAssignmentEmailAccountId(String emailAccountId, Pageable pageable);

    @Query("select c from YouTubeChannel c join fetch c.assignment a join fetch a.emailAccount join fetch c.user where c.id = :id")
    Optional<YouTubeChannel> findDetailedById(@Param("id") Long id);

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(Instant from, Instant to);
}
