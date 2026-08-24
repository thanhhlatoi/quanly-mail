package com.mycompany.myapp.dto.response;

import com.mycompany.myapp.domain.MonetizationApplicationStatus;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonetizationApplicationResponse {

    private Long id;
    private Long channelId;
    private String channelUrl;
    private String channelName;
    private Long assignmentId;
    private String emailAddress;
    private Long userId;
    private String userEmail;
    private MonetizationApplicationStatus status;
    private LocalDate submittedDate;
    private String eligibilityNote;
    private String submittedInfo;
    private String adminNote;
    private String errorMessage;
    private Long reviewedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
