package com.mycompany.myapp.dto.request;

import com.mycompany.myapp.domain.WorkLogStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogReviewRequest {

    @NotNull
    private WorkLogStatus status;

    private String reviewNote;
}
