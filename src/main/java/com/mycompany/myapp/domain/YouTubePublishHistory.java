package com.mycompany.myapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "youtube_publish_history",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_youtube_publish_history_video",
        columnNames = { "user_id", "channel_key", "video_hash" }
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YouTubePublishHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "channel_key", nullable = false, length = 255)
    private String channelKey;

    @Column(name = "channel_name", length = 255)
    private String channelName;

    @Column(name = "video_key", nullable = false, length = 512)
    private String videoKey;

    @Column(name = "video_hash", nullable = false, length = 64)
    private String videoHash;

    @Column(name = "video_index", nullable = false)
    private Integer videoIndex;

    @Column(name = "video_title", nullable = false, length = 500)
    private String videoTitle;

    @Column(name = "visibility", nullable = false, length = 20)
    private String visibility;

    @Column(name = "machine_name", nullable = false, length = 255)
    private String machineName;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
