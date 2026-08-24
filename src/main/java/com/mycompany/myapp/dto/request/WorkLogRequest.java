package com.mycompany.myapp.dto.request;

import com.mycompany.myapp.domain.WorkLogStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogRequest {

    @NotNull
    private Long assignmentId;

    private Long channelId;

    @NotNull
    private LocalDate workDate;

    @NotBlank
    private String content;

    @NotNull
    private WorkLogStatus status;
}
