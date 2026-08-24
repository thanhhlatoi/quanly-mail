package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.AssignmentUsageStatus;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.dto.request.EmailAccountAssignmentRequest;
import com.mycompany.myapp.dto.request.EmailAccountRequest;
import com.mycompany.myapp.dto.response.EmailAccountAssignmentResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface EmailAccountAssignmentService {
    EmailAccountAssignmentResponse assign(EmailAccountAssignmentRequest request, Long assignedById);
    List<EmailAccountAssignmentResponse> findMyActiveAssignments(Long userId);
    List<EmailAccountAssignmentResponse> findActiveByServiceType(EmailAccountServiceType serviceType);
    PageResponse<EmailAccountAssignmentResponse> findMyActivePage(Long userId, Pageable pageable);
    PageResponse<EmailAccountAssignmentResponse> findActivePageByServiceType(EmailAccountServiceType serviceType, Pageable pageable);
    PageResponse<EmailAccountAssignmentResponse> findActivePage(Pageable pageable);
    void cancel(Long assignmentId);
    EmailAccountAssignmentResponse updateUsageStatus(Long assignmentId, Long userId, AssignmentUsageStatus usageStatus);
    EmailAccountAssignmentResponse updateAssignedAccount(Long assignmentId, Long userId, EmailAccountRequest request);
}
