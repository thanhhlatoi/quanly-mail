package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Role;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.dto.request.RoleRequest;
import com.mycompany.myapp.dto.response.PageResponse;
import com.mycompany.myapp.dto.response.RoleResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleResponse create(RoleRequest request);

    RoleResponse update(Long id, RoleRequest request);

    Optional<RoleResponse> findById(Long id);

    List<RoleResponse> findAll();
    PageResponse<RoleResponse> findPage(Pageable pageable);

    void deleteById(Long id);

    void assignRoleToUser(Long roleId, Long userId);

    void removeRoleFromUser(Long roleId, Long userId);

    List<User> findUsersByRoleId(Long roleId);
}
