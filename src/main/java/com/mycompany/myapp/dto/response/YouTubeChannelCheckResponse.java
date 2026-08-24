package com.mycompany.myapp.dto.response;

import com.mycompany.myapp.domain.YouTubeCheckStatus;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeChannelCheckResponse {

    private Long id;
    private Long channelId;
    private String channelUrl;
    private String channelName;
    private Long userId;
    private String userEmail;
    private LocalDate checkDate;
    private YouTubeCheckStatus status;
    private Integer videoCount;
    private String note;
    private Instant checkedAt;
}
