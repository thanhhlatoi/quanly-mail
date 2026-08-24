package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeChannelRequest {

    @NotBlank
    private String channelUrl;

    private String channelName;

    @NotNull
    private Long youtubeAssignmentId;

    private String note;
}
