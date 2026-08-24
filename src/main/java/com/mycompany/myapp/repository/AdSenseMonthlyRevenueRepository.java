package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AdSenseMonthlyRevenue;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdSenseMonthlyRevenueRepository extends JpaRepository<AdSenseMonthlyRevenue, Long> {
    void deleteByLinkIdIn(List<Long> linkIds);
    Optional<AdSenseMonthlyRevenue> findByLinkIdAndRevenueMonth(Long linkId, String revenueMonth);

    @EntityGraph(
        attributePaths = {
            "link",
            "link.adsenseService",
            "link.adsenseService.emailAccount",
            "link.channel",
            "link.channel.user",
            "updatedBy",
        }
    )
    Page<AdSenseMonthlyRevenue> findAll(Pageable pageable);

    @EntityGraph(
        attributePaths = {
            "link",
            "link.adsenseService",
            "link.adsenseService.emailAccount",
            "link.channel",
            "link.channel.user",
            "updatedBy",
        }
    )
    Page<AdSenseMonthlyRevenue> findByLinkChannelUserId(Long userId, Pageable pageable);

    @Query(
        "select new com.mycompany.myapp.dto.response.AdSenseMonthlyTotalResponse(r.revenueMonth, s.id, ea.emailAddress, sum(r.amount), r.currency) from AdSenseMonthlyRevenue r join r.link l join l.adsenseService s join s.emailAccount ea where r.revenueMonth = :month and r.currency = :currency group by r.revenueMonth, s.id, ea.emailAddress, r.currency"
    )
    List<com.mycompany.myapp.dto.response.AdSenseMonthlyTotalResponse> totalsByService(
        @Param("month") String month,
        @Param("currency") String currency
    );
}
