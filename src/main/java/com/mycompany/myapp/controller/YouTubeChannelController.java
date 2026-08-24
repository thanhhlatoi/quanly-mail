package com.mycompany.myapp.controller;

import com.mycompany.myapp.dto.request.YouTubeChannelCheckRequest;
import com.mycompany.myapp.dto.request.YouTubeChannelRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.YouTubeChannelCheckResponse;
import com.mycompany.myapp.dto.response.YouTubeChannelResponse;
import com.mycompany.myapp.dto.response.YouTubeDailyStatsResponse;
import com.mycompany.myapp.dto.response.YouTubeUserDailyStatsResponse;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.YouTubeChannelService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/youtube-channels")
public class YouTubeChannelController {

    private final YouTubeChannelService channelService;

    public YouTubeChannelController(YouTubeChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    public ResponseEntity<YouTubeChannelResponse> create(@Valid @RequestBody YouTubeChannelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.create(request, currentUserId()));
    }

    @GetMapping("/me")
    public PageResponse<YouTubeChannelResponse> findMyChannels(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return channelService.findMyChannels(currentUserId(), pageable);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<YouTubeChannelResponse> findAllChannels(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long assignmentId,
        @RequestParam(required = false) String emailAccountId,
        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return channelService.findChannels(userId, assignmentId, emailAccountId, pageable);
    }

    @PostMapping("/{channelId}/checks")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public YouTubeChannelCheckResponse check(@PathVariable Long channelId, @Valid @RequestBody YouTubeChannelCheckRequest request) {
        return channelService.check(channelId, request, currentUserId());
    }

    @GetMapping("/checks")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<YouTubeChannelCheckResponse> findChecks(
        @RequestParam LocalDate date,
        @PageableDefault(size = 20, sort = "checkedAt") Pageable pageable
    ) {
        return channelService.findChecks(date, pageable);
    }

    @GetMapping("/stats/daily")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public YouTubeDailyStatsResponse dailyStats(@RequestParam LocalDate date) {
        return channelService.dailyStats(date);
    }

    @GetMapping("/stats/daily/by-user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<YouTubeUserDailyStatsResponse> dailyStatsByUser(@RequestParam LocalDate date) {
        return channelService.dailyStatsByUser(date);
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
    }
}
