package com.mycompany.myapp.dto.request;

import com.mycompany.myapp.domain.EmailAccountServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailServiceRequest {

    @NotBlank
    private String emailAccountId;

    @NotNull
    private EmailAccountServiceType serviceType;

    private String note;
    private Boolean active;
}
