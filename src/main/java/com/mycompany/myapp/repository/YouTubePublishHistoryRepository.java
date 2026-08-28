package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.YouTubePublishHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YouTubePublishHistoryRepository extends JpaRepository<YouTubePublishHistory, Long> {
    List<YouTubePublishHistory> findByUserIdAndChannelKeyOrderByPublishedAtDesc(Long userId, String channelKey);
    Optional<YouTubePublishHistory> findByUserIdAndChannelKeyAndVideoHash(Long userId, String channelKey, String videoHash);
}
