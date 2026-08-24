package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.AssignmentStatus;
import com.mycompany.myapp.domain.EmailAccountAssignment;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.YouTubeChannel;
import com.mycompany.myapp.domain.YouTubeChannelCheck;
import com.mycompany.myapp.domain.YouTubeChannelStatus;
import com.mycompany.myapp.domain.YouTubeCheckStatus;
import com.mycompany.myapp.dto.request.YouTubeChannelCheckRequest;
import com.mycompany.myapp.dto.request.YouTubeChannelRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.YouTubeChannelCheckResponse;
import com.mycompany.myapp.dto.response.YouTubeChannelResponse;
import com.mycompany.myapp.dto.response.YouTubeDailyStatsResponse;
import com.mycompany.myapp.dto.response.YouTubeUserDailyStatsResponse;
import com.mycompany.myapp.repository.EmailAccountAssignmentRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.YouTubeChannelCheckRepository;
import com.mycompany.myapp.repository.YouTubeChannelRepository;
import com.mycompany.myapp.service.YouTubeChannelService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class YouTubeChannelServiceImpl implements YouTubeChannelService {

    private final YouTubeChannelRepository channelRepository;
    private final YouTubeChannelCheckRepository checkRepository;
    private final EmailAccountAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public YouTubeChannelServiceImpl(
        YouTubeChannelRepository channelRepository,
        YouTubeChannelCheckRepository checkRepository,
        EmailAccountAssignmentRepository assignmentRepository,
        UserRepository userRepository
    ) {
        this.channelRepository = channelRepository;
        this.checkRepository = checkRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public YouTubeChannelResponse create(YouTubeChannelRequest request, Long userId) {
        EmailAccountAssignment assignment = assignmentRepository
            .findDetailedById(request.getYoutubeAssignmentId())
            .orElseThrow(() -> notFound("Không tìm thấy assignment Gmail"));
        if (assignment.getUser().getId() == null || !assignment.getUser().getId().equals(userId)) {
            throw forbidden("Bạn không được sử dụng assignment này");
        }
        if (assignment.getServiceType() != EmailAccountServiceType.YOUTUBE || assignment.getStatus() != AssignmentStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment không phải Gmail YouTube đang hoạt động");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> notFound("Không tìm thấy người dùng"));
        YouTubeChannel channel = new YouTubeChannel();
        channel.setChannelUrl(request.getChannelUrl().trim());
        channel.setChannelName(request.getChannelName());
        channel.setAssignment(assignment);
        channel.setUser(user);
        channel.setStatus(YouTubeChannelStatus.ACTIVE);
        channel.setNote(request.getNote());
        channel.setCreatedAt(Instant.now());
        return toResponse(channelRepository.save(channel));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<YouTubeChannelResponse> findMyChannels(Long userId, Pageable pageable) {
        return PageResponse.from(channelRepository.findByUserId(userId, pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<YouTubeChannelResponse> findAllChannels(Pageable pageable) {
        return PageResponse.from(channelRepository.findAll(pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<YouTubeChannelResponse> findChannels(Long userId, Long assignmentId, String emailAccountId, Pageable pageable) {
        var page =
            userId != null
                ? channelRepository.findByUserId(userId, pageable)
                : assignmentId != null
                  ? channelRepository.findByAssignmentId(assignmentId, pageable)
                  : emailAccountId != null
                    ? channelRepository.findByAssignmentEmailAccountId(emailAccountId, pageable)
                    : channelRepository.findAll(pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public YouTubeChannelCheckResponse check(Long channelId, YouTubeChannelCheckRequest request, Long adminId) {
        YouTubeChannel channel = channelRepository.findDetailedById(channelId).orElseThrow(() -> notFound("Không tìm thấy kênh YouTube"));
        User admin = userRepository.findById(adminId).orElseThrow(() -> notFound("Không tìm thấy admin"));
        YouTubeChannelCheck check = checkRepository
            .findByChannelIdAndCheckDate(channelId, request.getCheckDate())
            .orElseGet(YouTubeChannelCheck::new);
        check.setChannel(channel);
        check.setCheckedBy(admin);
        check.setCheckDate(request.getCheckDate());
        check.setStatus(request.getStatus());
        check.setVideoCount(request.getVideoCount());
        check.setNote(request.getNote());
        check.setCheckedAt(Instant.now());
        return toCheckResponse(checkRepository.save(check));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<YouTubeChannelCheckResponse> findChecks(LocalDate date, Pageable pageable) {
        return PageResponse.from(checkRepository.findByCheckDate(date, pageable).map(this::toCheckResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public YouTubeDailyStatsResponse dailyStats(LocalDate date) {
        Instant from = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return new YouTubeDailyStatsResponse(
            date,
            channelRepository.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(from, to),
            checkRepository.countByCheckDate(date),
            checkRepository.countByCheckDateAndStatus(date, YouTubeCheckStatus.PASS),
            checkRepository.countByCheckDateAndStatus(date, YouTubeCheckStatus.FAIL)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<YouTubeUserDailyStatsResponse> dailyStatsByUser(LocalDate date) {
        return checkRepository
            .findDailyStatsByUser(date)
            .stream()
            .map(stats ->
                new YouTubeUserDailyStatsResponse(
                    date,
                    stats.getUserId(),
                    stats.getUserEmail(),
                    stats.getChannelsChecked(),
                    stats.getChannelsPassed(),
                    stats.getChannelsFailed()
                )
            )
            .toList();
    }

    private YouTubeChannelResponse toResponse(YouTubeChannel channel) {
        return new YouTubeChannelResponse(
            channel.getId(),
            channel.getChannelUrl(),
            channel.getChannelName(),
            channel.getAssignment().getId(),
            channel.getAssignment().getEmailAccount().getEmailAddress(),
            channel.getUser().getId(),
            channel.getStatus(),
            channel.getNote(),
            channel.getCreatedAt()
        );
    }

    private YouTubeChannelCheckResponse toCheckResponse(YouTubeChannelCheck check) {
        return new YouTubeChannelCheckResponse(
            check.getId(),
            check.getChannel().getId(),
            check.getChannel().getChannelUrl(),
            check.getChannel().getChannelName(),
            check.getChannel().getUser().getId(),
            check.getChannel().getUser().getEmail(),
            check.getCheckDate(),
            check.getStatus(),
            check.getVideoCount(),
            check.getNote(),
            check.getCheckedAt()
        );
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }
}
