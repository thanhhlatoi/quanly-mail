package com.mycompany.myapp.controller;

import com.mycompany.myapp.dto.request.AdSenseMonthlyRevenueRequest;
import com.mycompany.myapp.dto.response.AdSenseMonthlyRevenueResponse;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.AdSenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/adsense")
public class UserAdSenseController {

    private final AdSenseService adSenseService;

    public UserAdSenseController(AdSenseService adSenseService) {
        this.adSenseService = adSenseService;
    }

    @PostMapping("/channels/{channelId}/monthly-revenue")
    public ResponseEntity<AdSenseMonthlyRevenueResponse> saveMonthlyRevenue(
        @PathVariable Long channelId,
        @Valid @RequestBody AdSenseMonthlyRevenueRequest request
    ) {
        Long userId = SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
        return ResponseEntity.status(HttpStatus.CREATED).body(adSenseService.saveMonthlyRevenue(channelId, request, userId));
    }
}
