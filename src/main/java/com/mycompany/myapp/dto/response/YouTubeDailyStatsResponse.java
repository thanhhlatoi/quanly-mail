package com.mycompany.myapp.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeDailyStatsResponse {

    private LocalDate date;
    private long channelsCreated;
    private long channelsChecked;
    private long channelsPassed;
    private long channelsFailed;
}
