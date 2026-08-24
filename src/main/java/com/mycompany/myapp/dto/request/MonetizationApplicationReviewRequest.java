package com.mycompany.myapp.dto.request;

import com.mycompany.myapp.domain.MonetizationApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonetizationApplicationReviewRequest {

    @NotNull
    private MonetizationApplicationStatus status;

    @Size(max = 10000)
    private String adminNote;

    @Size(max = 10000)
    private String errorMessage;
}
