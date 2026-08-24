package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.AssignmentStatus;
import com.mycompany.myapp.domain.EmailAccountAssignment;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.WorkLog;
import com.mycompany.myapp.domain.WorkLogStatus;
import com.mycompany.myapp.domain.YouTubeChannel;
import com.mycompany.myapp.dto.request.WorkLogRequest;
import com.mycompany.myapp.dto.request.WorkLogReviewRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.WorkLogResponse;
import com.mycompany.myapp.repository.EmailAccountAssignmentRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.WorkLogRepository;
import com.mycompany.myapp.repository.YouTubeChannelRepository;
import com.mycompany.myapp.service.WorkLogService;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkLogServiceImpl implements WorkLogService {

    private final WorkLogRepository workLogRepository;
    private final EmailAccountAssignmentRepository assignmentRepository;
    private final YouTubeChannelRepository channelRepository;
    private final UserRepository userRepository;

    public WorkLogServiceImpl(
        WorkLogRepository workLogRepository,
        EmailAccountAssignmentRepository assignmentRepository,
        YouTubeChannelRepository channelRepository,
        UserRepository userRepository
    ) {
        this.workLogRepository = workLogRepository;
        this.assignmentRepository = assignmentRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public WorkLogResponse create(WorkLogRequest request, Long userId) {
        EmailAccountAssignment assignment = assignmentRepository
            .findDetailedById(request.getAssignmentId())
            .orElseThrow(() -> notFound("Không tìm thấy assignment Gmail"));
        if (!assignment.getUser().getId().equals(userId)) {
            throw forbidden("Bạn không được ghi log cho assignment của user khác");
        }
        if (assignment.getStatus() != AssignmentStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment không còn hoạt động");
        }

        YouTubeChannel channel = null;
        if (request.getChannelId() != null) {
            channel = channelRepository.findDetailedById(request.getChannelId()).orElseThrow(() -> notFound("Không tìm thấy kênh YouTube"));
            if (!channel.getAssignment().getId().equals(assignment.getId()) || !channel.getUser().getId().equals(userId)) {
                throw forbidden("Kênh không thuộc assignment của bạn");
            }
        }
        if (assignment.getServiceType() != EmailAccountServiceType.YOUTUBE && channel != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ assignment YouTube mới được gắn kênh YouTube");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> notFound("Không tìm thấy người dùng"));
        WorkLog log = new WorkLog();
        log.setAssignment(assignment);
        log.setUser(user);
        log.setChannel(channel);
        log.setWorkDate(request.getWorkDate());
        log.setContent(request.getContent().trim());
        log.setStatus(validateUserStatus(request.getStatus()));
        log.setCreatedAt(Instant.now());
        return toResponse(workLogRepository.save(log));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkLogResponse> findMyLogs(Long userId, Pageable pageable) {
        return PageResponse.from(workLogRepository.findByUserId(userId, pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkLogResponse> findAll(WorkLogStatus status, Pageable pageable) {
        var page = status == null ? workLogRepository.findAll(pageable) : workLogRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public WorkLogResponse review(Long id, WorkLogReviewRequest request, Long adminId) {
        if (request.getStatus() != WorkLogStatus.APPROVED && request.getStatus() != WorkLogStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin chỉ được duyệt APPROVED hoặc REJECTED");
        }
        WorkLog log = workLogRepository.findDetailedById(id).orElseThrow(() -> notFound("Không tìm thấy work log"));
        User admin = userRepository.findById(adminId).orElseThrow(() -> notFound("Không tìm thấy admin"));
        log.setStatus(request.getStatus());
        log.setReviewNote(request.getReviewNote());
        log.setReviewedBy(admin);
        log.setReviewedAt(Instant.now());
        return toResponse(workLogRepository.save(log));
    }

    private WorkLogStatus validateUserStatus(WorkLogStatus status) {
        if (status != WorkLogStatus.IN_PROGRESS && status != WorkLogStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User chỉ được dùng IN_PROGRESS hoặc COMPLETED");
        }
        return status;
    }

    private WorkLogResponse toResponse(WorkLog log) {
        return new WorkLogResponse(
            log.getId(),
            log.getAssignment().getId(),
            log.getAssignment().getEmailAccount().getEmailAddress(),
            log.getChannel() == null ? null : log.getChannel().getId(),
            log.getChannel() == null ? null : log.getChannel().getChannelUrl(),
            log.getUser().getId(),
            log.getUser().getEmail(),
            log.getWorkDate(),
            log.getContent(),
            log.getStatus(),
            log.getReviewNote(),
            log.getReviewedBy() == null ? null : log.getReviewedBy().getId(),
            log.getCreatedAt(),
            log.getReviewedAt()
        );
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }
}
