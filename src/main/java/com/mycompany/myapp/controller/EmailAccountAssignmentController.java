package com.mycompany.myapp.controller;

import com.mycompany.myapp.domain.AssignmentUsageStatus;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.dto.request.EmailAccountAssignmentRequest;
import com.mycompany.myapp.dto.request.EmailAccountRequest;
import com.mycompany.myapp.dto.response.EmailAccountAssignmentResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.EmailAccountAssignmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email-account-assignments")
public class EmailAccountAssignmentController {

    private final EmailAccountAssignmentService assignmentService;

    public EmailAccountAssignmentController(EmailAccountAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmailAccountAssignmentResponse> assign(@Valid @RequestBody EmailAccountAssignmentRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assign(request, adminId));
    }

    @GetMapping("/me")
    public PageResponse<EmailAccountAssignmentResponse> findMyAssignments(
        @PageableDefault(size = 20, sort = "assignedAt") Pageable pageable
    ) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
        return assignmentService.findMyActivePage(userId, pageable);
    }

    @GetMapping("/admin/google-adsense")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<EmailAccountAssignmentResponse> findGoogleAdSenseAssignments(
        @PageableDefault(size = 20, sort = "assignedAt") Pageable pageable
    ) {
        return assignmentService.findActivePageByServiceType(EmailAccountServiceType.GOOGLE_ADSENSE, pageable);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<EmailAccountAssignmentResponse> findAllActiveAssignments(
        @PageableDefault(size = 20, sort = "assignedAt") Pageable pageable
    ) {
        return assignmentService.findActivePage(pageable);
    }

    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable Long assignmentId) {
        assignmentService.cancel(assignmentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{assignmentId}/usage")
    public ResponseEntity<EmailAccountAssignmentResponse> updateUsage(
        @PathVariable Long assignmentId,
        @RequestParam AssignmentUsageStatus status
    ) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
        return ResponseEntity.ok(assignmentService.updateUsageStatus(assignmentId, userId, status));
    }

    @PutMapping("/{assignmentId}/account")
    public ResponseEntity<EmailAccountAssignmentResponse> updateAssignedAccount(
        @PathVariable Long assignmentId,
        @Valid @RequestBody EmailAccountRequest request
    ) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
        return ResponseEntity.ok(assignmentService.updateAssignedAccount(assignmentId, userId, request));
    }
}
