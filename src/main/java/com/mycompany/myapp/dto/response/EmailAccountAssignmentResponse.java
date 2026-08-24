package com.mycompany.myapp.dto.response;

import com.mycompany.myapp.domain.AssignmentStatus;
import com.mycompany.myapp.domain.AssignmentUsageStatus;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAccountAssignmentResponse {

    private Long id;
    private String emailAccountId;
    private String emailAddress;
    private String passwordEncrypted;
    private String twoFactorSecretEncrypted;
    private String recoveryPhone;
    private String recoveryEmail;
    private String countryCode;
    private Integer year;
    private Long userId;
    private EmailAccountServiceType serviceType;
    private AssignmentStatus status;
    private AssignmentUsageStatus usageStatus;
    private Instant assignedAt;
    private String note;
}
