package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdSenseChannelLinkRequest {

    @JsonAlias({ "serviceId", "adsenseId" })
    private Long adsenseServiceId;

    @NotNull
    @JsonAlias({ "youtubeChannelId" })
    private Long channelId;

    /** Accept the assignment id used by the Gmail assignment screen. */
    private Long assignmentId;

    /** Accept the Gmail id used by the account/service screen. */
    private String emailAccountId;

    private String note;
}
