package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseChannelLinkRequest {

    @NotNull
    private Long adsenseServiceId;

    @NotNull
    private Long channelId;

    private String note;
}
