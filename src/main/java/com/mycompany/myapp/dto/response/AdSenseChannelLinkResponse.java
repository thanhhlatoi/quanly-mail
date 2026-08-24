package com.mycompany.myapp.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseChannelLinkResponse {

    private Long id;
    private Long adsenseServiceId;
    private String adsenseEmail;
    private Long channelId;
    private String channelUrl;
    private String channelName;
    private Long userId;
    private String userEmail;
    private boolean active;
    private String note;
    private Instant createdAt;
}
