package com.mycompany.myapp.controller;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.dto.request.NotificationRequest;
import com.mycompany.myapp.dto.response.*;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.security.SecurityUtils;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        Long senderId = currentUserId();
        User sender = userRepository.findById(senderId).orElseThrow(() -> notFound("Không tìm thấy người gửi"));
        if (request.getRecipientId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hãy chọn người nhận thông báo");
        }
        User recipient = userRepository.findById(request.getRecipientId()).orElseThrow(() -> notFound("Không tìm thấy người nhận"));
        Notification notification = new Notification(
            null,
            recipient,
            sender,
            request.getTitle().trim(),
            request.getContent().trim(),
            false,
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(notificationRepository.save(notification)));
    }

    @GetMapping("/me")
    public PageResponse<NotificationResponse> mine(@PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(notificationRepository.findByRecipientId(currentUserId(), pageable).map(this::toResponse));
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable Long id) {
        Notification notification = notificationRepository
            .findByIdAndRecipientId(id, currentUserId())
            .orElseThrow(() -> notFound("Không tìm thấy thông báo"));
        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    private NotificationResponse toResponse(Notification notification) {
        User recipient = notification.getRecipient();
        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getContent(),
            recipient.getId(),
            recipient.getFullName() != null ? recipient.getFullName() : recipient.getUserName(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserId().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập"));
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
