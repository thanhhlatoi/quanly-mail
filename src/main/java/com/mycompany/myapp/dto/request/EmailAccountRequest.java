package com.mycompany.myapp.dto.request;

import com.mycompany.myapp.domain.EmailAccountStatus;
import com.mycompany.myapp.domain.User;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailAccountRequest {

    private String emailAddress;
    private String passwordEncrypted;
    private String twoFactorSecretEncrypted;
    private String recoveryPhone;
    private String recoveryEmail;
    private String countryCode;
    private Integer year;
    private EmailAccountStatus status;
    private Long assignedUser;
    private BigDecimal purchasePrice;
    private String note;
    private Instant updatedAt;
    private Instant lastCheckedAt;
    private Instant lastLoginAt;
}
