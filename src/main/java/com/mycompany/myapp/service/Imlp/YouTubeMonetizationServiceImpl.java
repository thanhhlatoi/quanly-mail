package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.AssignmentStatus;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.MonetizationApplicationStatus;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.YouTubeChannel;
import com.mycompany.myapp.domain.YouTubeMonetizationApplication;
import com.mycompany.myapp.dto.request.MonetizationApplicationRequest;
import com.mycompany.myapp.dto.request.MonetizationApplicationReviewRequest;
import com.mycompany.myapp.dto.response.MonetizationApplicationResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.repository.YouTubeChannelRepository;
import com.mycompany.myapp.repository.YouTubeMonetizationApplicationRepository;
import com.mycompany.myapp.service.YouTubeMonetizationService;
import java.time.Instant;
import java.util.EnumSet;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class YouTubeMonetizationServiceImpl implements YouTubeMonetizationService {

    private final YouTubeMonetizationApplicationRepository applicationRepository;
    private final YouTubeChannelRepository channelRepository;
    private final UserRepository userRepository;

    public YouTubeMonetizationServiceImpl(
        YouTubeMonetizationApplicationRepository applicationRepository,
        YouTubeChannelRepository channelRepository,
        UserRepository userRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MonetizationApplicationResponse submit(MonetizationApplicationRequest request, Long userId) {
        YouTubeChannel channel = channelRepository
            .findDetailedById(request.getChannelId())
            .orElseThrow(() -> notFound("Không tìm thấy kênh YouTube"));
        if (!channel.getUser().getId().equals(userId)) {
            throw forbidden("Kênh không thuộc user hiện tại");
        }
        if (
            channel.getAssignment().getStatus() != AssignmentStatus.ACTIVE ||
            channel.getAssignment().getServiceType() != EmailAccountServiceType.YOUTUBE
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gmail assignment YouTube không còn hoạt động");
        }
        if (
            applicationRepository.existsByChannelIdAndStatusIn(
                channel.getId(),
                EnumSet.of(
                    MonetizationApplicationStatus.SUBMITTED,
                    MonetizationApplicationStatus.UNDER_REVIEW,
                    MonetizationApplicationStatus.SENT_TO_YOUTUBE
                )
            )
        ) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kênh đang có hồ sơ bật kiếm tiền chờ xử lý");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> notFound("Không tìm thấy user"));
        YouTubeMonetizationApplication application = new YouTubeMonetizationApplication();
        application.setChannel(channel);
        application.setAssignment(channel.getAssignment());
        application.setUser(user);
        application.setStatus(MonetizationApplicationStatus.SUBMITTED);
        application.setSubmittedDate(request.getSubmittedDate());
        application.setEligibilityNote(request.getEligibilityNote());
        application.setSubmittedInfo(request.getSubmittedInfo());
        application.setCreatedAt(Instant.now());
        application.setUpdatedAt(Instant.now());
        return toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MonetizationApplicationResponse> findMyApplications(Long userId, Pageable pageable) {
        return PageResponse.from(applicationRepository.findByUserId(userId, pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MonetizationApplicationResponse> findAll(MonetizationApplicationStatus status, Pageable pageable) {
        var page = status == null ? applicationRepository.findAll(pageable) : applicationRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public MonetizationApplicationResponse review(Long id, MonetizationApplicationReviewRequest request, Long adminId) {
        if (request.getStatus() == MonetizationApplicationStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin không được chuyển hồ sơ về SUBMITTED");
        }
        YouTubeMonetizationApplication application = applicationRepository
            .findDetailedById(id)
            .orElseThrow(() -> notFound("Không tìm thấy hồ sơ bật kiếm tiền"));
        User admin = userRepository.findById(adminId).orElseThrow(() -> notFound("Không tìm thấy admin"));
        application.setStatus(request.getStatus());
        application.setAdminNote(request.getAdminNote());
        application.setErrorMessage(request.getErrorMessage());
        application.setReviewedBy(admin);
        application.setUpdatedAt(Instant.now());
        return toResponse(applicationRepository.save(application));
    }

    private MonetizationApplicationResponse toResponse(YouTubeMonetizationApplication application) {
        return new MonetizationApplicationResponse(
            application.getId(),
            application.getChannel().getId(),
            application.getChannel().getChannelUrl(),
            application.getChannel().getChannelName(),
            application.getAssignment().getId(),
            application.getAssignment().getEmailAccount().getEmailAddress(),
            application.getUser().getId(),
            application.getUser().getEmail(),
            application.getStatus(),
            application.getSubmittedDate(),
            application.getEligibilityNote(),
            application.getSubmittedInfo(),
            application.getAdminNote(),
            application.getErrorMessage(),
            application.getReviewedBy() == null ? null : application.getReviewedBy().getId(),
            application.getCreatedAt(),
            application.getUpdatedAt()
        );
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }
}
