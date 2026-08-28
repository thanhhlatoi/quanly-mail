package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubePublishHistoryRequest {

    @NotBlank
    @Size(max = 255)
    private String channelKey;

    @Size(max = 255)
    private String channelName;

    @NotBlank
    @Size(max = 512)
    private String videoKey;

    @NotNull
    private Integer videoIndex;

    @NotBlank
    @Size(max = 500)
    private String videoTitle;

    @NotBlank
    @Size(max = 20)
    private String visibility;

    @NotBlank
    @Size(max = 255)
    private String machineName;
}
