package com.mycompany.myapp.dto.request;

import com.mycompany.myapp.domain.YouTubeCheckStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YouTubeChannelCheckRequest {

    @NotNull
    private LocalDate checkDate;

    @NotNull
    private YouTubeCheckStatus status;

    private Integer videoCount;
    private String note;
}
