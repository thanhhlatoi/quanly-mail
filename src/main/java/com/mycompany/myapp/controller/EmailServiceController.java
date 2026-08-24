package com.mycompany.myapp.controller;

import com.mycompany.myapp.domain.EmailAccountServiceType;
import com.mycompany.myapp.dto.request.EmailServiceRequest;
import com.mycompany.myapp.dto.response.EmailServiceResponse;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.service.EmailServiceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EmailServiceController {

    private final EmailServiceService serviceService;

    public EmailServiceController(EmailServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<EmailServiceResponse> create(@Valid @RequestBody EmailServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.create(request));
    }

    @GetMapping
    public PageResponse<EmailServiceResponse> findAll(
        @RequestParam(required = false) EmailAccountServiceType serviceType,
        @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return serviceService.findPage(serviceType, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailServiceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.of(serviceService.findById(id));
    }

    @PutMapping("/{id}")
    public EmailServiceResponse update(@PathVariable Long id, @Valid @RequestBody EmailServiceRequest request) {
        return serviceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
