package com.mycompany.myapp.controller;

import com.mycompany.myapp.dto.request.AdSenseChannelLinkRequest;
import com.mycompany.myapp.dto.request.AdSenseRevenueRequest;
import com.mycompany.myapp.dto.response.AdSenseChannelLinkResponse;
import com.mycompany.myapp.dto.response.AdSenseMonthlyRevenueResponse;
import com.mycompany.myapp.dto.response.AdSenseRevenueResponse;
import com.mycompany.myapp.dto.response.AdSenseRevenueSummaryResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.AdSenseService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/adsense")
public class AdSenseController {

    private final AdSenseService adSenseService;

    public AdSenseController(AdSenseService adSenseService) {
        this.adSenseService = adSenseService;
    }

    @PostMapping("/links")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdSenseChannelLinkResponse> linkChannel(@Valid @RequestBody AdSenseChannelLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adSenseService.linkChannel(request));
    }

    @GetMapping("/links")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<AdSenseChannelLinkResponse> findLinks(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return adSenseService.findLinks(pageable);
    }

    @DeleteMapping("/links/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> unlinkChannel(@PathVariable Long id) {
        adSenseService.unlinkChannel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/links/{linkId}/revenues")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdSenseRevenueResponse> saveRevenue(
        @PathVariable Long linkId,
        @Valid @RequestBody AdSenseRevenueRequest request
    ) {
        Long adminId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
        return ResponseEntity.status(HttpStatus.CREATED).body(adSenseService.saveRevenue(linkId, request, adminId));
    }

    @GetMapping("/revenues")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<AdSenseRevenueResponse> findRevenues(@PageableDefault(size = 20, sort = "revenueDate") Pageable pageable) {
        return adSenseService.findRevenues(pageable);
    }

    @GetMapping("/revenues/summary")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdSenseRevenueSummaryResponse summarize(
        @RequestParam LocalDate from,
        @RequestParam LocalDate to,
        @RequestParam(defaultValue = "USD") String currency
    ) {
        return adSenseService.summarize(from, to, currency);
    }

    @GetMapping("/monthly-revenues")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<AdSenseMonthlyRevenueResponse> findMonthlyRevenues(
        @RequestParam(required = false) Long userId,
        @PageableDefault(size = 20, sort = "revenueMonth") Pageable pageable
    ) {
        return adSenseService.findMonthlyRevenues(userId, pageable);
    }

    @GetMapping("/monthly-revenues/totals")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<com.mycompany.myapp.dto.response.AdSenseMonthlyTotalResponse> monthlyTotals(
        @RequestParam String month,
        @RequestParam(defaultValue = "USD") String currency
    ) {
        return adSenseService.monthlyTotals(month, currency);
    }
}
