package com.mycompany.myapp.service;

import com.mycompany.myapp.dto.request.PermissionsRequest;
import com.mycompany.myapp.dto.request.RoleRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.PermissionsResponse;
import com.mycompany.myapp.dto.response.RoleResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface PermissionsService {
    PermissionsResponse create(PermissionsRequest request);

    PermissionsResponse update(Long id, PermissionsRequest request);

    Optional<PermissionsResponse> findById(Long id);

    List<PermissionsResponse> findAll();
    PageResponse<PermissionsResponse> findPage(Pageable pageable);

    void deleteById(Long id);
}
