package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.EmailAccountStatus;
import com.mycompany.myapp.dto.request.EmailAccountRequest;
import com.mycompany.myapp.dto.response.EmailAccountImportResponse;
import com.mycompany.myapp.dto.response.EmailAccountResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EmailAccountService {
    EmailAccountResponse create(EmailAccountRequest request);
    List<EmailAccountResponse> createBulk(List<EmailAccountRequest> requests);
    EmailAccountImportResponse importCsv(MultipartFile file);
    EmailAccountResponse update(String id, EmailAccountRequest request);
    Optional<EmailAccountResponse> findById(String id);
    List<EmailAccountResponse> findAll();
    List<EmailAccountResponse> findAvailableAccounts();
    List<EmailAccountResponse> findByStatus(EmailAccountStatus status);
    PageResponse<EmailAccountResponse> findPage(EmailAccountStatus status, Pageable pageable, boolean includeAdsense);
    void changeStatus(String id, EmailAccountStatus status);
    void delete(String id);
}
