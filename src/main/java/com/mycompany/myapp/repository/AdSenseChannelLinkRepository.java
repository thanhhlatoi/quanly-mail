package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AdSenseChannelLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdSenseChannelLinkRepository extends JpaRepository<AdSenseChannelLink, Long> {
    List<AdSenseChannelLink> findByAdsenseServiceEmailAccountId(String emailAccountId);

    @EntityGraph(attributePaths = { "adsenseService", "adsenseService.emailAccount", "channel", "channel.user" })
    Page<AdSenseChannelLink> findAll(Pageable pageable);

    @EntityGraph(attributePaths = { "adsenseService", "adsenseService.emailAccount", "channel", "channel.user" })
    Optional<AdSenseChannelLink> findDetailedById(Long id);

    @EntityGraph(attributePaths = { "adsenseService", "adsenseService.emailAccount", "channel", "channel.user" })
    Optional<AdSenseChannelLink> findByChannelId(Long channelId);
}
