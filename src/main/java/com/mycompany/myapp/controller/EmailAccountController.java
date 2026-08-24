package com.mycompany.myapp.controller;

import com.mycompany.myapp.domain.EmailAccountStatus;
import com.mycompany.myapp.dto.request.EmailAccountRequest;
import com.mycompany.myapp.dto.response.EmailAccountImportResponse;
import com.mycompany.myapp.dto.response.EmailAccountResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.service.EmailAccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/email_account")
public class EmailAccountController {

    private final EmailAccountService emailAccountService;

    public EmailAccountController(EmailAccountService emailAccountService) {
        this.emailAccountService = emailAccountService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmailAccountResponse> create(@Valid @RequestBody EmailAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailAccountService.create(request));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EmailAccountResponse>> createBulk(@RequestBody List<@Valid EmailAccountRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailAccountService.createBulk(requests));
    }

    @PostMapping(value = "/import-csv", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmailAccountImportResponse> importCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(emailAccountService.importCsv(file));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<EmailAccountResponse> findAll(
        @RequestParam(required = false) EmailAccountStatus status,
        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return emailAccountService.findPage(status, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmailAccountResponse> findById(@PathVariable String id) {
        return ResponseEntity.of(emailAccountService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EmailAccountResponse update(@PathVariable String id, @Valid @RequestBody EmailAccountRequest request) {
        return emailAccountService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> changeStatus(@PathVariable String id, @RequestParam EmailAccountStatus status) {
        emailAccountService.changeStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        emailAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
