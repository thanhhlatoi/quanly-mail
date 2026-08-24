package com.mycompany.myapp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonetizationApplicationRequest {

    @NotNull
    private Long channelId;

    @NotNull
    private LocalDate submittedDate;

    @Size(max = 5000)
    private String eligibilityNote;

    @Size(max = 10000)
    private String submittedInfo;
}
