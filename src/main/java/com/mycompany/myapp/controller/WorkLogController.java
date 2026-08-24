package com.mycompany.myapp.controller;

import com.mycompany.myapp.domain.WorkLogStatus;
import com.mycompany.myapp.dto.request.WorkLogRequest;
import com.mycompany.myapp.dto.request.WorkLogReviewRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.WorkLogResponse;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.WorkLogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/work-logs")
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @PostMapping
    public ResponseEntity<WorkLogResponse> create(@Valid @RequestBody WorkLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workLogService.create(request, currentUserId()));
    }

    @GetMapping("/me")
    public PageResponse<WorkLogResponse> findMyLogs(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return workLogService.findMyLogs(currentUserId(), pageable);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<WorkLogResponse> findAll(
        @RequestParam(required = false) WorkLogStatus status,
        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return workLogService.findAll(status, pageable);
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public WorkLogResponse review(@PathVariable Long id, @Valid @RequestBody WorkLogReviewRequest request) {
        return workLogService.review(id, request, currentUserId());
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
    }
}
