package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.YouTubeChannelCheck;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YouTubeChannelCheckRepository extends JpaRepository<YouTubeChannelCheck, Long> {
    @Query(
        "select c.channel.user.id as userId, c.channel.user.email as userEmail, count(c) as channelsChecked, sum(case when c.status = com.mycompany.myapp.domain.YouTubeCheckStatus.PASS then 1 else 0 end) as channelsPassed, sum(case when c.status = com.mycompany.myapp.domain.YouTubeCheckStatus.FAIL then 1 else 0 end) as channelsFailed from YouTubeChannelCheck c where c.checkDate = :checkDate group by c.channel.user.id, c.channel.user.email"
    )
    List<YouTubeUserDailyStatsProjection> findDailyStatsByUser(@Param("checkDate") LocalDate checkDate);

    Optional<YouTubeChannelCheck> findByChannelIdAndCheckDate(Long channelId, LocalDate checkDate);

    @Query(
        value = "select c from YouTubeChannelCheck c join fetch c.channel ch join fetch ch.user where c.checkDate = :checkDate",
        countQuery = "select count(c) from YouTubeChannelCheck c where c.checkDate = :checkDate"
    )
    Page<YouTubeChannelCheck> findByCheckDate(@Param("checkDate") LocalDate checkDate, Pageable pageable);

    long countByCheckDate(LocalDate checkDate);

    long countByCheckDateAndStatus(LocalDate checkDate, com.mycompany.myapp.domain.YouTubeCheckStatus status);
}
