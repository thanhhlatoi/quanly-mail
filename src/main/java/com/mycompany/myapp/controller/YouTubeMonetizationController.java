package com.mycompany.myapp.controller;

import com.mycompany.myapp.domain.MonetizationApplicationStatus;
import com.mycompany.myapp.dto.request.MonetizationApplicationRequest;
import com.mycompany.myapp.dto.request.MonetizationApplicationReviewRequest;
import com.mycompany.myapp.dto.response.MonetizationApplicationResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.YouTubeMonetizationService;
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
@RequestMapping("/api/v1/youtube-monetization")
public class YouTubeMonetizationController {

    private final YouTubeMonetizationService monetizationService;

    public YouTubeMonetizationController(YouTubeMonetizationService monetizationService) {
        this.monetizationService = monetizationService;
    }

    @PostMapping
    public ResponseEntity<MonetizationApplicationResponse> submit(@Valid @RequestBody MonetizationApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(monetizationService.submit(request, currentUserId()));
    }

    @GetMapping("/me")
    public PageResponse<MonetizationApplicationResponse> findMine(@PageableDefault(size = 20, sort = "updatedAt") Pageable pageable) {
        return monetizationService.findMyApplications(currentUserId(), pageable);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<MonetizationApplicationResponse> findAll(
        @RequestParam(required = false) MonetizationApplicationStatus status,
        @PageableDefault(size = 20, sort = "updatedAt") Pageable pageable
    ) {
        return monetizationService.findAll(status, pageable);
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public MonetizationApplicationResponse review(@PathVariable Long id, @Valid @RequestBody MonetizationApplicationReviewRequest request) {
        return monetizationService.review(id, request, currentUserId());
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
    }
}
