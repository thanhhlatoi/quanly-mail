package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.AssignmentStatus;
import com.mycompany.myapp.domain.AssignmentUsageStatus;
import com.mycompany.myapp.domain.EmailAccount;
import com.mycompany.myapp.domain.EmailAccountAssignment;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.EmailAccountAssignmentRequest;
import com.mycompany.myapp.dto.request.EmailAccountRequest;
import com.mycompany.myapp.dto.response.EmailAccountAssignmentResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.repository.EmailAccountAssignmentRepository;
import com.mycompany.myapp.repository.EmailAccountRepository;
import com.mycompany.myapp.repository.EmailServiceRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.EmailAccountAssignmentService;
import com.mycompany.myapp.service.EmailAccountService;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailAccountAssignmentServiceImpl implements EmailAccountAssignmentService {

    private final EmailAccountAssignmentRepository assignmentRepository;
    private final EmailAccountRepository emailAccountRepository;
    private final UserRepository userRepository;
    private final EmailAccountService emailAccountService;
    private final EmailServiceRepository emailServiceRepository;

    public EmailAccountAssignmentServiceImpl(
        EmailAccountAssignmentRepository assignmentRepository,
        EmailAccountRepository emailAccountRepository,
        UserRepository userRepository,
        EmailAccountService emailAccountService,
        EmailServiceRepository emailServiceRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.emailAccountRepository = emailAccountRepository;
        this.userRepository = userRepository;
        this.emailAccountService = emailAccountService;
        this.emailServiceRepository = emailServiceRepository;
    }

    @Override
    @Transactional
    public EmailAccountAssignmentResponse assign(EmailAccountAssignmentRequest request, Long assignedById) {
        if (
            assignmentRepository.existsByEmailAccountIdAndServiceTypeAndStatus(
                request.getEmailAccountId(),
                request.getServiceType(),
                AssignmentStatus.ACTIVE
            )
        ) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Gmail đã được giao cho dịch vụ này");
        }
        EmailAccount emailAccount = emailAccountRepository
            .findById(request.getEmailAccountId())
            .orElseThrow(() -> notFound("Không tìm thấy Gmail"));
        if (emailServiceRepository.existsByEmailAccountIdAndServiceType(request.getEmailAccountId(), EmailAccountServiceType.GOOGLE_ADSENSE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Gmail AdSense không được phép giao trong kho Gmail");
        }
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> notFound("Không tìm thấy người nhận Gmail"));
        User assignedBy = userRepository.findById(assignedById).orElseThrow(() -> notFound("Không tìm thấy người giao Gmail"));

        EmailAccountAssignment assignment = new EmailAccountAssignment();
        assignment.setEmailAccount(emailAccount);
        assignment.setUser(user);
        assignment.setAssignedBy(assignedBy);
        assignment.setServiceType(request.getServiceType());
        assignment.setAssignedAt(Instant.now());
        assignment.setNote(request.getNote());
        assignment.setStatus(AssignmentStatus.ACTIVE);
        assignment.setUsageStatus(AssignmentUsageStatus.ASSIGNED);
        return toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAccountAssignmentResponse> findMyActiveAssignments(Long userId) {
        return assignmentRepository.findActiveByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAccountAssignmentResponse> findActiveByServiceType(EmailAccountServiceType serviceType) {
        return assignmentRepository.findActiveByServiceType(serviceType).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmailAccountAssignmentResponse> findMyActivePage(Long userId, Pageable pageable) {
        return PageResponse.from(assignmentRepository.findActivePageByUserId(userId, pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmailAccountAssignmentResponse> findActivePageByServiceType(
        EmailAccountServiceType serviceType,
        Pageable pageable
    ) {
        return PageResponse.from(assignmentRepository.findActivePageByServiceType(serviceType, pageable).map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmailAccountAssignmentResponse> findActivePage(Pageable pageable) {
        return PageResponse.from(assignmentRepository.findActivePage(pageable).map(this::toResponse));
    }

    @Override
    @Transactional
    public void cancel(Long assignmentId) {
        EmailAccountAssignment assignment = assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> notFound("Không tìm thấy assignment"));
        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.setUnassignedAt(Instant.now());
        assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public EmailAccountAssignmentResponse updateUsageStatus(Long assignmentId, Long userId, AssignmentUsageStatus usageStatus) {
        EmailAccountAssignment assignment = assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> notFound("Không tìm thấy assignment"));
        if (!assignment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không được cập nhật Gmail của user khác");
        }
        if (assignment.getStatus() != AssignmentStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Assignment không còn hoạt động");
        }
        assignment.setUsageStatus(usageStatus);
        return toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public EmailAccountAssignmentResponse updateAssignedAccount(Long assignmentId, Long userId, EmailAccountRequest request) {
        EmailAccountAssignment assignment = assignmentRepository
            .findById(assignmentId)
            .orElseThrow(() -> notFound("Không tìm thấy assignment"));
        if (!assignment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không được sửa Gmail của user khác");
        }
        if (assignment.getStatus() != AssignmentStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Assignment không còn hoạt động");
        }
        emailAccountService.update(assignment.getEmailAccount().getId(), request);
        return toResponse(assignmentRepository.findById(assignmentId).orElseThrow());
    }

    private EmailAccountAssignmentResponse toResponse(EmailAccountAssignment assignment) {
        return new EmailAccountAssignmentResponse(
            assignment.getId(),
            assignment.getEmailAccount().getId(),
            assignment.getEmailAccount().getEmailAddress(),
            assignment.getEmailAccount().getPasswordEncrypted(),
            assignment.getEmailAccount().getTwoFactorSecretEncrypted(),
            assignment.getEmailAccount().getRecoveryPhone(),
            assignment.getEmailAccount().getRecoveryEmail(),
            assignment.getEmailAccount().getCountryCode(),
            assignment.getEmailAccount().getYear(),
            assignment.getUser().getId(),
            assignment.getServiceType(),
            assignment.getStatus(),
            assignment.getUsageStatus(),
            assignment.getAssignedAt(),
            assignment.getNote()
        );
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
