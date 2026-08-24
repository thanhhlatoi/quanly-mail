package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "adsense_monthly_revenues",
    uniqueConstraints = @UniqueConstraint(name = "uk_adsense_monthly_revenue", columnNames = { "link_id", "revenue_month" })
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseMonthlyRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "link_id", nullable = false)
    private AdSenseChannelLink link;

    @Column(name = "revenue_month", nullable = false, length = 7)
    private String revenueMonth;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public YearMonth getYearMonth() {
        return YearMonth.parse(revenueMonth);
    }
}
