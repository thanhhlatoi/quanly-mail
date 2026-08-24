package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AdSenseRevenue;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public interface AdSenseRevenueRepository extends JpaRepository<AdSenseRevenue, Long> {
    void deleteByLinkIdIn(List<Long> linkIds);
    Optional<AdSenseRevenue> findByLinkIdAndRevenueDate(Long linkId, LocalDate revenueDate);

    @EntityGraph(
        attributePaths = { "link", "link.adsenseService", "link.adsenseService.emailAccount", "link.channel", "link.channel.user" }
    )
    Page<AdSenseRevenue> findAll(Pageable pageable);

    @Query("select coalesce(sum(r.amount), 0) from AdSenseRevenue r where r.revenueDate between :from and :to and r.currency = :currency")
    BigDecimal sumAmount(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("currency") String currency);
}
