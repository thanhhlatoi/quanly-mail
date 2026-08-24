package com.mycompany.myapp.dto.response;

import com.mycompany.myapp.domain.WorkLogStatus;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkLogResponse {

    private Long id;
    private Long assignmentId;
    private String emailAddress;
    private Long channelId;
    private String channelUrl;
    private Long userId;
    private String userEmail;
    private LocalDate workDate;
    private String content;
    private WorkLogStatus status;
    private String reviewNote;
    private Long reviewedBy;
    private Instant createdAt;
    private Instant reviewedAt;
}
