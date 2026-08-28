package com.mycompany.myapp.controller;

import com.mycompany.myapp.domain.YouTubePublishHistory;
import com.mycompany.myapp.dto.request.YouTubePublishHistoryRequest;
import com.mycompany.myapp.dto.response.YouTubePublishHistoryResponse;
import com.mycompany.myapp.repository.YouTubePublishHistoryRepository;
import com.mycompany.myapp.security.SecurityUtils;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/youtube-publish-history")
@PreAuthorize("isAuthenticated()")
public class YouTubePublishHistoryController {

    private final YouTubePublishHistoryRepository historyRepository;

    public YouTubePublishHistoryController(YouTubePublishHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @GetMapping
    public List<YouTubePublishHistoryResponse> findByChannel(@RequestParam String channelKey) {
        Long userId = currentUserId();
        return historyRepository
            .findByUserIdAndChannelKeyOrderByPublishedAtDesc(userId, channelKey)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @PostMapping
    public ResponseEntity<YouTubePublishHistoryResponse> create(@Valid @RequestBody YouTubePublishHistoryRequest request) {
        Long userId = currentUserId();
        String videoHash = sha256(request.getVideoKey().trim());
        var existing = historyRepository.findByUserIdAndChannelKeyAndVideoHash(userId, request.getChannelKey(), videoHash);
        if (existing.isPresent()) return ResponseEntity.ok(toResponse(existing.orElseThrow()));

        var history = new YouTubePublishHistory();
        history.setUserId(userId);
        history.setChannelKey(request.getChannelKey().trim());
        history.setChannelName(trimToNull(request.getChannelName()));
        history.setVideoKey(request.getVideoKey().trim());
        history.setVideoHash(videoHash);
        history.setVideoIndex(request.getVideoIndex());
        history.setVideoTitle(request.getVideoTitle().trim());
        history.setVisibility(request.getVisibility().trim());
        history.setMachineName(request.getMachineName().trim());
        history.setPublishedAt(Instant.now());
        history.setCreatedAt(Instant.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(historyRepository.save(history)));
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId().orElseThrow(() -> new IllegalStateException("JWT không chứa userId"));
    }

    private YouTubePublishHistoryResponse toResponse(YouTubePublishHistory history) {
        return new YouTubePublishHistoryResponse(
            history.getId(),
            history.getUserId(),
            history.getChannelKey(),
            history.getChannelName(),
            history.getVideoKey(),
            history.getVideoHash(),
            history.getVideoIndex(),
            history.getVideoTitle(),
            history.getVisibility(),
            history.getMachineName(),
            history.getPublishedAt()
        );
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(digest.length * 2);
            for (byte item : digest) result.append("%02x".formatted(item));
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Không hỗ trợ SHA-256", exception);
        }
    }
}
