package com.mycompany.myapp.controller;

import com.mycompany.myapp.dto.request.PermissionsRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.PermissionsResponse;
import com.mycompany.myapp.service.PermissionsService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionsController {

    private final PermissionsService permissionsService;

    public PermissionsController(PermissionsService permissionsService) {
        this.permissionsService = permissionsService;
    }

    @PostMapping
    public ResponseEntity<PermissionsResponse> create(@Valid @RequestBody PermissionsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionsService.create(request));
    }

    @GetMapping
    public PageResponse<PermissionsResponse> findAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return permissionsService.findPage(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionsResponse> findById(@PathVariable Long id) {
        return ResponseEntity.of(permissionsService.findById(id));
    }

    @PutMapping("/{id}")
    public PermissionsResponse update(@PathVariable Long id, @Valid @RequestBody PermissionsRequest request) {
        return permissionsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
