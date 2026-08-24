package com.mycompany.myapp.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeUserDailyStatsResponse {

    private LocalDate date;
    private Long userId;
    private String userEmail;
    private long channelsChecked;
    private long channelsPassed;
    private long channelsFailed;
}
