package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.dto.request.EmailServiceRequest;
import com.mycompany.myapp.dto.response.EmailServiceResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface EmailServiceService {
    EmailServiceResponse create(EmailServiceRequest request);
    EmailServiceResponse update(Long id, EmailServiceRequest request);
    Optional<EmailServiceResponse> findById(Long id);
    PageResponse<EmailServiceResponse> findPage(EmailAccountServiceType serviceType, Pageable pageable);
    void delete(Long id);
}
