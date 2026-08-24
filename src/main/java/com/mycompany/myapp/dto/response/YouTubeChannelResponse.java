package com.mycompany.myapp.dto.response;

import com.mycompany.myapp.domain.YouTubeChannelStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeChannelResponse {

    private Long id;
    private String channelUrl;
    private String channelName;
    private Long assignmentId;
    private String emailAddress;
    private Long userId;
    private YouTubeChannelStatus status;
    private String note;
    private Instant createdAt;
}
