package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "youtube_channel_checks",
    uniqueConstraints = @UniqueConstraint(name = "uk_youtube_channel_check_day", columnNames = { "channel_id", "check_date" })
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeChannelCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private YouTubeChannel channel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checked_by", nullable = false)
    private User checkedBy;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private YouTubeCheckStatus status;

    @Column(name = "video_count")
    private Integer videoCount;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;
}
