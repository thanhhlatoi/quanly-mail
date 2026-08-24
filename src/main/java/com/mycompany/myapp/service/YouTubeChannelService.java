package com.mycompany.myapp.service;

import com.mycompany.myapp.dto.request.YouTubeChannelCheckRequest;
import com.mycompany.myapp.dto.request.YouTubeChannelRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.YouTubeChannelCheckResponse;
import com.mycompany.myapp.dto.response.YouTubeChannelResponse;
import com.mycompany.myapp.dto.response.YouTubeDailyStatsResponse;
import com.mycompany.myapp.dto.response.YouTubeUserDailyStatsResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface YouTubeChannelService {
    YouTubeChannelResponse create(YouTubeChannelRequest request, Long userId);
    PageResponse<YouTubeChannelResponse> findMyChannels(Long userId, Pageable pageable);
    PageResponse<YouTubeChannelResponse> findAllChannels(Pageable pageable);
    PageResponse<YouTubeChannelResponse> findChannels(Long userId, Long assignmentId, String emailAccountId, Pageable pageable);
    YouTubeChannelCheckResponse check(Long channelId, YouTubeChannelCheckRequest request, Long adminId);
    PageResponse<YouTubeChannelCheckResponse> findChecks(LocalDate date, Pageable pageable);
    YouTubeDailyStatsResponse dailyStats(LocalDate date);
    List<YouTubeUserDailyStatsResponse> dailyStatsByUser(LocalDate date);
}
