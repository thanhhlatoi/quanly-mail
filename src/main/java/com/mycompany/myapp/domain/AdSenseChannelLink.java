package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "adsense_channel_links",
    uniqueConstraints = @UniqueConstraint(name = "uk_adsense_channel_link_channel", columnNames = "channel_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseChannelLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adsense_service_id", nullable = false)
    private EmailService adsenseService;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private YouTubeChannel channel;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
