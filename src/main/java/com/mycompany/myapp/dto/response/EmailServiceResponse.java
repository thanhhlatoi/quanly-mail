package com.mycompany.myapp.dto.response;

import com.mycompany.myapp.domain.EmailAccountServiceType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailServiceResponse {

    private Long id;
    private String emailAccountId;
    private String emailAddress;
    private EmailAccountServiceType serviceType;
    private String note;
    private boolean active;
    private Instant createdAt;
}
