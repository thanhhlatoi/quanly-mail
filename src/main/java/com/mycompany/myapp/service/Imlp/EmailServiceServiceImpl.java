package com.mycompany.myapp.service.Imlp;

import com.mycompany.myapp.domain.EmailAccount;
import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.domain.EmailService;
import com.mycompany.myapp.dto.request.EmailServiceRequest;
import com.mycompany.myapp.dto.response.EmailServiceResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.repository.EmailAccountRepository;
import com.mycompany.myapp.repository.EmailServiceRepository;
import com.mycompany.myapp.service.EmailServiceService;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailServiceServiceImpl implements EmailServiceService {

    private final EmailServiceRepository serviceRepository;
    private final EmailAccountRepository emailAccountRepository;

    public EmailServiceServiceImpl(EmailServiceRepository serviceRepository, EmailAccountRepository emailAccountRepository) {
        this.serviceRepository = serviceRepository;
        this.emailAccountRepository = emailAccountRepository;
    }

    @Override
    @Transactional
    public EmailServiceResponse create(EmailServiceRequest request) {
        if (serviceRepository.existsByEmailAccountIdAndServiceType(request.getEmailAccountId(), request.getServiceType())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Gmail đã được khai báo cho dịch vụ này");
        }
        EmailService service = new EmailService();
        copy(request, service);
        service.setCreatedAt(Instant.now());
        return toResponse(serviceRepository.save(service));
    }

    @Override
    @Transactional
    public EmailServiceResponse update(Long id, EmailServiceRequest request) {
        EmailService service = serviceRepository.findById(id).orElseThrow(() -> notFound("Không tìm thấy dịch vụ của Gmail"));
        if (
            !service.getEmailAccount().getId().equals(request.getEmailAccountId()) || service.getServiceType() != request.getServiceType()
        ) {
            boolean duplicate = serviceRepository.existsByEmailAccountIdAndServiceType(
                request.getEmailAccountId(),
                request.getServiceType()
            );
            if (duplicate) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Gmail đã được khai báo cho dịch vụ này");
            }
        }
        copy(request, service);
        return toResponse(serviceRepository.save(service));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailServiceResponse> findById(Long id) {
        return serviceRepository.findById(id).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmailServiceResponse> findPage(EmailAccountServiceType serviceType, Pageable pageable) {
        var page = serviceType == null ? serviceRepository.findAll(pageable) : serviceRepository.findByServiceType(serviceType, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw notFound("Không tìm thấy dịch vụ của Gmail");
        }
        serviceRepository.deleteById(id);
    }

    private void copy(EmailServiceRequest request, EmailService service) {
        EmailAccount emailAccount = emailAccountRepository
            .findById(request.getEmailAccountId())
            .orElseThrow(() -> notFound("Không tìm thấy Gmail"));
        service.setEmailAccount(emailAccount);
        service.setServiceType(request.getServiceType());
        service.setNote(request.getNote());
        if (request.getActive() != null) {
            service.setActive(request.getActive());
        }
    }

    private EmailServiceResponse toResponse(EmailService service) {
        return new EmailServiceResponse(
            service.getId(),
            service.getEmailAccount().getId(),
            service.getEmailAccount().getEmailAddress(),
            service.getServiceType(),
            service.getNote(),
            service.isActive(),
            service.getCreatedAt()
        );
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
