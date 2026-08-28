package com.mycompany.myapp.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubePublishHistoryResponse {

    private Long id;
    private Long userId;
    private String channelKey;
    private String channelName;
    private String videoKey;
    private String videoHash;
    private Integer videoIndex;
    private String videoTitle;
    private String visibility;
    private String machineName;
    private Instant publishedAt;
}
